package com.raiden.tool.http.processor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.TypeUtil;
import com.google.gson.Gson;
import com.raiden.tool.http.HttpBootStrap;
import com.raiden.tool.http.annotation.Body;
import com.raiden.tool.http.annotation.Param;
import com.raiden.tool.http.annotation.PathParam;
import com.raiden.tool.http.enums.HttpMethod;
import com.raiden.tool.http.enums.RequestEnum;
import com.raiden.tool.http.interceptor.HttpClientInterceptor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author fishlikewater@126.com
 * @version V1.0.0
 * @since 2021年12月26日 18:59
 **/
@Slf4j
public class DefaultHttpClientProcessor implements HttpClientProcessor {
    private static final Gson gson = new Gson();

    @SneakyThrows(Throwable.class)
    @Override
    public Object handler(MethodArgsBean methodArgsBean, Object[] args) {
        HttpMethod method = methodArgsBean.getRequestMethod();
        RequestEnum requestEnum = methodArgsBean.getMediaType();
        final Parameter[] parameters = methodArgsBean.getUrlParameters();
        String url = methodArgsBean.getRealUrl();
        final Class<?> returnType = methodArgsBean.getReturnType();
        final Type typeArgument = methodArgsBean.getTypeArgument();
        final HttpClientInterceptor interceptor = methodArgsBean.getInterceptor();
        Map<String, String> headMap = methodArgsBean.getHeadMap();
        Map<String, String> paramMap = MapUtil.newHashMap();
        Map<String, String> paramPath = MapUtil.newHashMap();
        final HttpClient httpClient = HttpBootStrap.getHttpClient(methodArgsBean.getClassName());
        Object bodyObject = null;
        /* 构建请求参数*/
        if (ObjectUtil.isNotNull(parameters)) {
            for (int i = 0; i < parameters.length; i++) {
                Param param = parameters[i].getAnnotation(Param.class);
                if (ObjectUtil.isNotNull(param)) {
                    paramMap.put(param.value(), (String) args[i]);
                    continue;
                }
                PathParam pathParam = parameters[i].getAnnotation(PathParam.class);
                if (ObjectUtil.isNotNull(pathParam)) {
                    paramPath.put(pathParam.value(), (String) args[i]);
                    continue;
                }
                Body body = parameters[i].getAnnotation(Body.class);
                if (ObjectUtil.isNotNull(body)) {
                    bodyObject = args[i];
                }
            }
            if (!paramPath.isEmpty()){
                url = StrFormatter.format(url, paramPath, true);
            }
        }
        if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
            final HttpRequest.Builder builder = HttpRequest.newBuilder().GET().uri(URI.create(getRequestUrl(url, paramMap)));
            headMap.forEach(builder::header);
            return convert(returnType, builder.build(), typeArgument, interceptor, httpClient);
        }
        if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            if (requestEnum == RequestEnum.JSON) {
                HttpRequest.BodyPublisher requestBody = HttpRequest.BodyPublishers.ofString(gson.toJson(bodyObject));
                final HttpRequest.Builder builder = HttpRequest.newBuilder().POST(requestBody).uri(URI.create(url));
                headMap.forEach(builder::header);
                builder.header("Content-Type", "application/json;charset=utf-8");
                return convert(returnType, builder.build(), typeArgument, interceptor, httpClient);
            }
            if (requestEnum == RequestEnum.FORM) {
                HttpRequest.BodyPublisher requestBody = null;
                if (bodyObject != null) {
                    StringBuilder params = new StringBuilder("rd=").append(Math.random());
                    if (bodyObject instanceof Map<?, ?> map) {
                        for (Map.Entry<?, ?> item : map.entrySet()) {
                            String param = "&" + item.getKey().toString().trim() + "=" + item.getValue().toString().trim();
                            params.append(param);
                        }
                    }
                    requestBody = HttpRequest.BodyPublishers.ofString(params.toString());
                }
                final HttpRequest.Builder builder = HttpRequest.newBuilder().POST(requestBody).uri(URI.create(url));
                headMap.forEach(builder::header);
                builder.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                return convert(returnType, builder.build(), typeArgument, interceptor, httpClient);
            }
            if (requestEnum == RequestEnum.FILE) {
                HttpRequest.BodyPublisher requestBody = null;
                if (bodyObject != null) {
                    if (bodyObject instanceof String filePath) {
                        requestBody = HttpRequest.BodyPublishers.ofFile(Path.of(filePath));
                    } else if (bodyObject instanceof byte[] bytes) {
                        requestBody = HttpRequest.BodyPublishers.ofByteArray(bytes);
                    } else if (bodyObject instanceof File file) {
                        requestBody = HttpRequest.BodyPublishers.ofInputStream(() -> {
                            try {
                                return new FileInputStream(file);
                            } catch (FileNotFoundException e) {

                                log.error("文件不存在", e);
                            }
                            return null;
                        });
                    }else if (bodyObject instanceof InputStream inputStream){
                        requestBody = HttpRequest.BodyPublishers.ofInputStream(()->inputStream);
                    }
                }
                final HttpRequest.Builder builder = HttpRequest.newBuilder().POST(requestBody).uri(URI.create(url));
                headMap.forEach(builder::header);
                builder.header("Content-Type", "multipart/form-data");
                return convert(returnType, builder.build(), typeArgument, interceptor, httpClient);
            }

        }
        return "";
    }


    private Object convert(Class<?> returnType, HttpRequest httpRequest, Type typeArgument, HttpClientInterceptor interceptor, HttpClient httpClient) throws IOException, InterruptedException {
        if (Objects.nonNull(interceptor)){
            httpRequest = interceptor.requestBefore(httpRequest);
        }
        if (returnType.isAssignableFrom(CompletableFuture.class)){
            //异步
            if (typeArgument.getClass().isAssignableFrom(String.class)){
                return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(res-> requestAfter(res, interceptor).body());
            }else if (typeArgument.getClass().isAssignableFrom(byte[].class)){
                return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray()).thenApply(res-> requestAfter(res, interceptor).body());
            }else {
                return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(response->{
                    final String body = response.body();
                    return gson.fromJson(body, TypeUtil.getClass(typeArgument));
                });
            }
        }else {
            //同步
            if (returnType.isAssignableFrom(String.class)){
                final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                return requestAfter(response, interceptor).body();
            } else if (returnType.isAssignableFrom(byte[].class)){
                final HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
                return requestAfter(response, interceptor).body();
            }else {
                final HttpResponse<Object> response = httpClient.send(httpRequest, (responseInfo) -> new ResponseJsonHandlerSubscriber<>(responseInfo.headers(), returnType, gson));
                return requestAfter(response, interceptor).body();
            }
        }
    }


    private <T> HttpResponse<T> requestAfter(HttpResponse<T> response, HttpClientInterceptor interceptor){
        if (Objects.nonNull(interceptor)){
            return interceptor.requestAfter(response);
        }
        return response;
    }


    private static String getRequestUrl(String url, Map<String, String> map) {
        if (map.isEmpty()) {
            return url;
        } else {
            StringBuilder newUrl = new StringBuilder(url);
            if (!url.contains("?")) {
                newUrl.append("?rd=").append(Math.random());
            }

            for (Map.Entry<String, String> item : map.entrySet()) {
                try {
                    String param = "&" + item.getKey().trim() + "=" + URLEncoder.encode(item.getValue().trim(), StandardCharsets.UTF_8);
                    newUrl.append(param);
                } catch (Exception e) {
                    log.error("拼接参数异常", e);
                }
            }

            return newUrl.toString();
        }
    }

}

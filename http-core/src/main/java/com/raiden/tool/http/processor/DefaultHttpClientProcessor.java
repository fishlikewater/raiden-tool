package com.raiden.tool.http.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.json.JSONUtil;
import com.raiden.tool.http.HttpBootStrap;
import com.raiden.tool.http.enums.HttpMethod;
import com.raiden.tool.http.file.MultipartData;
import com.raiden.tool.http.interceptor.HttpClientInterceptor;
import com.raiden.tool.http.log.LogInterceptor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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

    private static final LogInterceptor logInterceptor = new LogInterceptor();
    @SneakyThrows(Throwable.class)
    @Override
    public Object handler(HttpMethod method, Map<String, String> headMap, Class<?> returnType, Type typeArgument, boolean form, String url,
                          Map<String, String> paramMap, Object bodyObject, HttpClientInterceptor interceptor, MultipartData multipartData, HttpClient httpClient) {
        if (method == HttpMethod.GET || method == HttpMethod.DELETE) {
            final HttpRequest.Builder builder = HttpRequest.newBuilder().GET().uri(URI.create(getRequestUrl(url, paramMap)));
            headMap.forEach(builder::header);
            return convert(returnType, builder.build(), typeArgument, interceptor, httpClient);
        }
        if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            if (form) {
                final HttpRequest httpRequest = handlerForm(bodyObject, headMap, url);
                return convert(returnType, httpRequest, typeArgument, interceptor, httpClient);
            }else {
                final HttpRequest httpRequest;
                if (Objects.nonNull(multipartData)){
                    httpRequest = handlerFile(bodyObject, headMap, url, multipartData);
                }else {
                    httpRequest = handlerJson(bodyObject, headMap, url);
                }
                return convert(returnType, httpRequest, typeArgument, interceptor, httpClient);
            }
        }
        return "";
    }

    private HttpRequest handlerJson(Object bodyObject, Map<String, String> headMap, String url) {
        HttpRequest.BodyPublisher requestBody = HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr(bodyObject));
        final HttpRequest.Builder builder = HttpRequest.newBuilder().POST(requestBody).uri(URI.create(url));
        headMap.forEach(builder::header);
        builder.header("Content-Type", "application/json;charset=utf-8");
        return builder.build();
    }

    private HttpRequest handlerForm(Object bodyObject, Map<String, String> headMap, String url) {
        HttpRequest.BodyPublisher requestBody = null;
        if (bodyObject != null) {
            StringBuilder params = new StringBuilder("rd=").append(Math.random());
            if (bodyObject instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> item : map.entrySet()) {
                    String param = "&" + item.getKey().toString().trim() + "=" + item.getValue().toString().trim();
                    params.append(param);
                }
            }else {
                final Map<String, Object> map = BeanUtil.beanToMap(bodyObject);
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
        return builder.build();
    }


    private HttpRequest handlerFile(Object bodyObject, Map<String, String> headMap, String url, MultipartData multipartData) {
        final HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url));
        headMap.forEach(builder::header);
        HttpRequest.BodyPublisher requestBody = new MultiFileBodyProvider(multipartData, bodyObject);
        builder.POST(requestBody);
        return builder.build();
    }

    private Object convert(Class<?> returnType, HttpRequest httpRequest, Type typeArgument, HttpClientInterceptor interceptor, HttpClient httpClient) throws IOException, InterruptedException {
        if (Objects.nonNull(interceptor)) {
            httpRequest = interceptor.requestBefore(httpRequest);
        }
        if (HttpBootStrap.getLogConfig().isEnableLog()){
            httpRequest = logInterceptor.requestBefore(httpRequest);
        }
        if (returnType.isAssignableFrom(CompletableFuture.class)) {
            //异步
            if (typeArgument.getClass().isAssignableFrom(String.class)) {
                return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).thenApply(res -> requestAfter(res, interceptor).body());
            }
            if (typeArgument.getClass().isAssignableFrom(byte[].class)) {
                return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray()).thenApply(res -> requestAfter(res, interceptor).body());
            }
            return httpClient.sendAsync(httpRequest, (responseInfo) -> new ResponseJsonHandlerSubscriber<>(responseInfo.headers(), TypeUtil.getClass(typeArgument))).thenApply(res -> requestAfter(res, interceptor).body());

        } else {
            //同步
            if (returnType.isAssignableFrom(String.class)) {
                final HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                return requestAfter(response, interceptor).body();
            }
            if (returnType.isAssignableFrom(byte[].class)) {
                final HttpResponse<byte[]> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
                return requestAfter(response, interceptor).body();
            }
            final HttpResponse<Object> response = httpClient.send(httpRequest, (responseInfo) -> new ResponseJsonHandlerSubscriber<>(responseInfo.headers(), returnType));
            return requestAfter(response, interceptor).body();

        }
    }


    private <T> HttpResponse<T> requestAfter(HttpResponse<T> response, HttpClientInterceptor interceptor) {
        if (Objects.nonNull(interceptor)) {
            response = interceptor.requestAfter(response);
        }
        if (HttpBootStrap.getLogConfig().isEnableLog()){
            response = logInterceptor.requestAfter(response);
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

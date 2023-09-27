package com.raiden.tool.http.log;

import com.raiden.tool.http.HttpBootStrap;
import com.raiden.tool.http.interceptor.HttpClientInterceptor;
import com.raiden.tool.http.uttils.ByteBufferUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Flow;

/**
 * 日志拦截器配置
 *
 * @author fishlikewater@126.com
 * @since 2023年09月25日 15:00
 **/
@Slf4j
public class LogInterceptor implements HttpClientInterceptor {
    @Override
    public HttpRequest requestBefore(HttpRequest httpRequest) {
        log.info("=====================================begin==========================================");
        final LogConfig.LogLevel logLevel = HttpBootStrap.getLogConfig().getLogLevel();
        final HttpHeaders headers = httpRequest.headers();
        log.info("请求地址: {}", httpRequest.uri().toString());
        log.info("请求方法: {}", httpRequest.method());
        recordHeads(logLevel, headers);
        if (logLevel == LogConfig.LogLevel.DETAIL){
            httpRequest.bodyPublisher().ifPresent(bodyPublisher -> {
                final Optional<String> contentType = headers.firstValue("Content-Type");
                contentType.ifPresent(s -> {
                    if (!s.equals("multipart/form-data")){
                        bodyPublisher.subscribe(new Flow.Subscriber<>() {

                            private final List<ByteBuffer> received = new ArrayList<>();
                            Flow.Subscription subscription;
                            @Override
                            public void onSubscribe(Flow.Subscription subscription) {
                                if (this.subscription != null) {
                                    subscription.cancel();
                                    return;
                                }
                                this.subscription = subscription;
                                subscription.request(Long.MAX_VALUE);
                            }
                            @Override
                            public void onNext(ByteBuffer item) {
                                assert item.hasRemaining();
                                received.add(item);
                            }
                            @Override
                            public void onError(Throwable throwable) {
                                log.error("", throwable);
                            }
                            @Override
                            public void onComplete() {
                                final byte[] bytes = ByteBufferUtils.join(received);
                                final String jsonStr = new String(bytes, StandardCharsets.UTF_8);
                                log.info("请求数据: {}", jsonStr);
                            }
                        });

                    }
                });
            });
        }
        return httpRequest;
    }

    @Override
    public <T> HttpResponse<T> requestAfter(HttpResponse<T> response) {
        log.info("响应信息: ");
        final LogConfig.LogLevel logLevel = HttpBootStrap.getLogConfig().getLogLevel();
        final int state = response.statusCode();
        log.info("{}<-{}", state, response.uri().toString());
        final HttpHeaders headers = response.headers();
        recordHeads(logLevel, headers);
        if (logLevel == LogConfig.LogLevel.DETAIL){
            final String responseStr = response.body().toString();
            log.info("响应数据: {}", responseStr);
        }
        log.info("=======================================END===========================================");


        return response;
    }

    private void recordHeads(LogConfig.LogLevel logLevel, HttpHeaders headers) {
        if (logLevel == LogConfig.LogLevel.HEADS || logLevel == LogConfig.LogLevel.DETAIL){
            final Map<String, List<String>> map = headers.map();
            map.forEach((k, v)-> log.info("{}: {}", k, v));
        }
    }
}

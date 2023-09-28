package com.raiden.tool.http.processor;

import cn.hutool.json.JSONUtil;
import com.raiden.tool.http.uttils.ByteBufferUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 10:14
 **/
@Slf4j
public class ResponseJsonHandlerSubscriber<T> implements HttpResponse.BodySubscriber<T> {
    Flow.Subscription subscription;
    private final CompletableFuture<T> result = new CompletableFuture<>();
    private final HttpHeaders headers;

    private final Class<?> clazz;

    private final List<ByteBuffer> received = new ArrayList<>();

    ResponseJsonHandlerSubscriber(HttpHeaders headers, Class<?> clazz){
        this.headers = headers;
        this.clazz = clazz;
    }

    @Override
    public CompletionStage<T> getBody() {
        return result;
    }

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
    public void onNext(List<ByteBuffer> items) {
        assert ByteBufferUtils.hasRemaining(items);
        received.addAll(items);
    }

    @Override
    public void onError(Throwable throwable) {
        received.clear();
        result.completeExceptionally(throwable);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onComplete() {
        final byte[] bytes = ByteBufferUtils.join(received);
        final Charset charset = ByteBufferUtils.charsetFrom(headers);
        final String jsonStr = new String(bytes, charset);
        Object bean;
        if (clazz.isAssignableFrom(String.class) || clazz.isAssignableFrom(Number.class)){
            bean = jsonStr;
        }else {
            bean = JSONUtil.toBean(jsonStr, clazz);
        }
        result.complete((T) bean);
    }

}

package com.raiden.tool.http.processor;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final Gson gson;

    private final List<ByteBuffer> received = new ArrayList<>();

    ResponseJsonHandlerSubscriber(HttpHeaders headers, Class<?> clazz, Gson gson){
        this.headers = headers;
        this.clazz = clazz;
        this.gson = gson;
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
        assert hasRemaining(items);
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
        final byte[] bytes = join(received);
        final Charset charset = charsetFrom(headers);
        final String jsonStr = new String(bytes, charset);
        final Object bean = gson.fromJson(jsonStr, clazz);
        result.complete((T) bean);
    }

    private static byte[] join(List<ByteBuffer> bytes) {
        int size = remaining(bytes);
        byte[] res = new byte[size];
        int from = 0;
        for (ByteBuffer b : bytes) {
            int l = b.remaining();
            b.get(res, from, l);
            from += l;
        }
        return res;
    }

    private static int remaining(List<ByteBuffer> buffs) {
        long remain = 0;
        for (ByteBuffer buf : buffs) {
            remain += buf.remaining();
            if (remain > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("too many bytes");
            }
        }
        return (int) remain;
    }


    private boolean hasRemaining(List<ByteBuffer> buffs) {
        for (ByteBuffer buf : buffs) {
            if (buf.hasRemaining())
                return true;
        }
        return false;
    }
    private static Charset charsetFrom(HttpHeaders headers) {
        String type = headers.firstValue("Content-type")
                .orElse("text/html; charset=utf-8");
        int i = type.indexOf(";");
        if (i >= 0) type = type.substring(i+1);
        try {
            Pattern pattern = Pattern.compile("charset=([a-zA-Z0-9-]+)");
            Matcher matcher = pattern.matcher(type);
            if (matcher.find()) {
                return Charset.forName(matcher.group(1));
            }
            return StandardCharsets.UTF_8;
        } catch (Throwable x) {
            log.warn("Can't find charset in {} ", type, x);
            return StandardCharsets.UTF_8;
        }
    }

}

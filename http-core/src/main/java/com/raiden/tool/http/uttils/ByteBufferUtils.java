package com.raiden.tool.http.uttils;

import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpHeaders;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fishlikewater@126.com
 * @since 2023年09月25日 16:46
 **/
@Slf4j
public class ByteBufferUtils {

    public static byte[] join(List<ByteBuffer> bytes) {
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


    public static int remaining(List<ByteBuffer> buffs) {
        long remain = 0;
        for (ByteBuffer buf : buffs) {
            remain += buf.remaining();
            if (remain > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("too many bytes");
            }
        }
        return (int) remain;
    }


    public static boolean hasRemaining(List<ByteBuffer> buffs) {
        for (ByteBuffer buf : buffs) {
            if (buf.hasRemaining())
                return true;
        }
        return false;
    }
    public static Charset charsetFrom(HttpHeaders headers) {
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

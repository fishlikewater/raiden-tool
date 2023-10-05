package com.raiden.tool.core.utils;

import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * <p>
 *  包装requestId 传递到子线程
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2022年12月05日 19:16
 **/
public class ThreadMdcUtil {

    private static final String REQUEST_ID = "REQUEST_ID";
    private static final String TRACE_ID = "traceId";

    /** 获取唯一性标识 */
    public static String generateTraceId() {
        return  UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

    public static void setRequestId() {
        if (MDC.get(REQUEST_ID) == null) {
            final String id = generateTraceId();
            MDC.put(REQUEST_ID, id);
            MDC.put(TRACE_ID, id);
        }
    }

    /**
     * 用于父线程向线程池中提交任务时，将自身MDC中的数据复制给子线程
     *
     * @param callable 回调函数
     * @param context 上下文
     */
    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setRequestId();
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    /**
     * 用于父线程向线程池中提交任务时，将自身MDC中的数据复制给子线程
     *
     * @param runnable 子线程
     * @param context 上下文
     */
    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setRequestId();
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}

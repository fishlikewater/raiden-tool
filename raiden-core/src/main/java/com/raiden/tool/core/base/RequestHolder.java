package com.raiden.tool.core.base;

/**
 * @author <p>fishlikewater@126.com</p>
 * @version V1.0
 * @since 2021年12月03日 23:28
 *
 **/
public class RequestHolder {

    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    public static void setRequestId(String requestId){
        THREAD_LOCAL.set(requestId);
    }

    public static String getRequestId(){
        return THREAD_LOCAL.get();
    }

    public static void remove(){
        THREAD_LOCAL.remove();
    }
}

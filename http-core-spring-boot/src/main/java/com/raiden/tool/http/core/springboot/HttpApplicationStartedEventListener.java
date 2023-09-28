package com.raiden.tool.http.core.springboot;

import com.raiden.tool.http.HttpBootStrap;
import com.raiden.tool.http.interceptor.HttpClientInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;

/**
 * 接口注入
 *
 * @author fishlikewater@126.com
 * @since 2023年09月22日 11:12
 **/
@Component
@Slf4j
public class HttpApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartedEvent  event) {
        final String[] namesForType = event.getApplicationContext().getBeanNamesForType(HttpClient.class);
        if (namesForType.length>0){
            final HttpClient httpClient = (HttpClient) event.getApplicationContext().getBean(namesForType[0]);
            HttpBootStrap.registerHttpClient("default", httpClient);
        }
        final String[] interceptors = event.getApplicationContext().getBeanNamesForType(HttpClientInterceptor.class);
        for (String interceptor : interceptors) {
            final HttpClientInterceptor httpClientInterceptor = (HttpClientInterceptor) event.getApplicationContext().getBean(interceptor);
            HttpBootStrap.setHttpClientInterceptor(httpClientInterceptor);
        }
    }
}
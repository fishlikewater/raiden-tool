package httptest;


import com.raiden.tool.http.interceptor.HttpClientInterceptor;

import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 13:41
 **/
public class MyInterceptor implements HttpClientInterceptor {
    @Override
    public HttpRequest requestBefore(HttpRequest httpRequest) {
        System.out.println("自定义拦截器--请求");

        return httpRequest;
    }

    @Override
    public <T> HttpResponse<T> requestAfter(HttpResponse<T> response) {
        System.out.println("自定义拦截器--响应");
        return response;
    }
}

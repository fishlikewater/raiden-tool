package com.raiden.tool.http.source;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *     HttpClient 注册容器
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 10:14
 **/
public class SourceHttpClientRegistry {

    private final Map<String, HttpClient> httpClientMap;

    private final List<SourceHttpClientRegister> registrars;

    public SourceHttpClientRegistry(List<SourceHttpClientRegister> registrars) {
        this.registrars = registrars;
        this.httpClientMap = new HashMap<>(4);
    }

    public void init() {
        if (registrars == null) {
            return;
        }
        registrars.forEach(registrar -> registrar.register(this));
    }

    public void register(String name, HttpClient httpClient) {
        httpClientMap.put(name, httpClient);
    }

    public HttpClient get(String name) {
        HttpClient httpClient = httpClientMap.get(name);
        if (Objects.isNull(httpClient)){
            return httpClientMap.get("default");
        }
        return httpClient;
    }

}

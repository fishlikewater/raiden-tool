package com.raiden.tool.http.core.springboot;

import cn.hutool.core.util.StrUtil;
import com.raiden.tool.http.interceptor.PredRequest;
import com.raiden.tool.http.MethodArgsBean;
import lombok.RequiredArgsConstructor;

import java.net.URI;

/**
 * 注册服务名处理
 *
 * @author fishlikewater@126.com
 * @since 2023年09月26日 14:44
 **/
@RequiredArgsConstructor
public class ServiceChoose implements PredRequest {

    private final ServiceInstanceChooser serviceInstanceChooser;
    @Override
    public void handler(MethodArgsBean methodArgsBean) {
        if (StrUtil.isBlank(methodArgsBean.getUrl())){
            final String serverName = methodArgsBean.getServerName();
            if (StrUtil.isBlank(serverName)){
                throw new RuntimeException("没有配置请求地址");
            }
            final URI uri = serviceInstanceChooser.choose(serverName);
            methodArgsBean.setUrl(uri.toString());
        }
    }
}

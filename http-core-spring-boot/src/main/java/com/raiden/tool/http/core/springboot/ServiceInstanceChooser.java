package com.raiden.tool.http.core.springboot;

import java.net.URI;

/**
 * 注册服务选择
 *
 * @author fishlikewater@126.com
 * @since 2023年09月26日 14:33
 **/
@FunctionalInterface
public interface ServiceInstanceChooser {

    URI choose(String serviceId);

    class NoValidServiceInstanceChooser implements ServiceInstanceChooser {

        @Override
        public URI choose(String serviceId) {
            throw new RuntimeException("没有配置服务选择实现类，请配置它");
        }
    }

}

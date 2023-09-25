package com.raiden.tool.http.core.springboot;

import com.raiden.tool.http.HttpBootStrap;
import com.raiden.tool.http.processor.SourceHttpClientRegistrar;
import com.raiden.tool.http.processor.SourceHttpClientRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月24日 12:37
 **/
@Slf4j
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration
@EnableConfigurationProperties(HttpConfigProperties.class)
public class HttpAutoConfig {


    @Bean
    public SourceHttpClientRegistrar sourceHttpClientRegistrar(){
        return (registry) -> {
            final HttpClient defaultClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(60)).version(HttpClient.Version.HTTP_1_1).build();
            registry.register("default", defaultClient);
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public SourceHttpClientRegistry sourceOkHttpClientRegistry(
            @Autowired(required = false) List<SourceHttpClientRegistrar> sourceOkHttpClientRegistrars) {
        final SourceHttpClientRegistry sourceHttpClientRegistry = new SourceHttpClientRegistry(sourceOkHttpClientRegistrars);
        sourceHttpClientRegistry.init();
        HttpBootStrap.setSourceHttpClientRegistry(sourceHttpClientRegistry);
        return sourceHttpClientRegistry;
    }


}

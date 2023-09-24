package com.raiden.tool.http.core.springboot;

import com.raiden.tool.http.enums.ProxyEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月24日 12:35
 **/

@Data
@ConfigurationProperties("com.raiden.http")
public class HttpConfigProperties {

    private boolean enableLog;


    private ProxyEnum proxyType = ProxyEnum.JDK;


}

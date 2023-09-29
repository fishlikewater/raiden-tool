package com.raiden.tool.http;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 日志配置
 *
 * @author fishlikewater@126.com
 * @since 2023年09月25日 14:55
 **/
@Data
@Accessors(chain = true)
public class LogConfig {

    private boolean enableLog;

    private LogLevel logLevel = LogLevel.BASIC;

    public static enum LogLevel{
        BASIC,
        HEADS,
        DETAIL;
    }

}

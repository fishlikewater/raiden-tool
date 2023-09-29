/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月28日 21:43
 **/
module com.raiden.tool.http {
    requires cn.hutool.core;
    requires io.github.classgraph;
    requires lombok;
    requires java.net.http;
    requires cn.hutool.json;
    requires cglib;
    requires org.slf4j;

    exports com.raiden.tool.http;
    exports com.raiden.tool.http.enums;
    exports com.raiden.tool.http.proxy;
    exports com.raiden.tool.http.annotation;
    exports com.raiden.tool.http.interceptor;
    exports com.raiden.tool.http.source;
}
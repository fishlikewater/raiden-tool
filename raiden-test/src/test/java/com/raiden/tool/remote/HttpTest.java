package com.raiden.tool.remote;

import com.raiden.tool.MyInterceptor;
import com.raiden.tool.http.annotation.*;


/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月23日 18:40
 **/
@HttpServer(url = "www.baidu.com", sourceHttpClient = "third")
@Interceptor(MyInterceptor.class)
public interface HttpTest {

    @RequireLine(path = "/")
    String test();

    @GET("/{id}")
    String test1(@PathParam("id")String id);

    @GET("/")
    String test2(@Param("id")String id);



}

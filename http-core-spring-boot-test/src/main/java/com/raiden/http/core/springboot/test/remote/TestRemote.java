package com.raiden.http.core.springboot.test.remote;


import com.raiden.http.core.springboot.test.interceptor.MyInterceptor;
import com.raiden.tool.http.annotation.*;
import com.raiden.tool.http.enums.HttpMethod;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月22日 19:28
 **/

@HttpServer(url = "http://www.baidu.com")
@Interceptor(MyInterceptor.class)
public interface TestRemote {


    @RequireLine(path = "/", method = HttpMethod.GET)
    String test(@Param("id")String  id);


    @RequireLine(path = "/{id}", method = HttpMethod.GET)
    String test2(@PathParam("id")String  id);

}

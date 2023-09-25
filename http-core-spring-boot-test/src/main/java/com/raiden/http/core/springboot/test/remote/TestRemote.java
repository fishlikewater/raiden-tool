package com.raiden.http.core.springboot.test.remote;


import com.raiden.http.core.springboot.test.enitty.LoginBo;
import com.raiden.tool.http.annotation.*;
import com.raiden.tool.http.enums.HttpMethod;
import com.raiden.tool.http.enums.RequestEnum;

/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月22日 19:28
 **/

@HttpServer(url = "http://www.baidu.com")
public interface TestRemote {


    @RequireLine(path = "/", method = HttpMethod.GET)
    String test(@Param("id")String  id);


    @RequireLine(path = "/{id}", method = HttpMethod.POST, mediaType = RequestEnum.FORM)
    String test2(@PathParam("id")String  id, @Body LoginBo loginBo);

}

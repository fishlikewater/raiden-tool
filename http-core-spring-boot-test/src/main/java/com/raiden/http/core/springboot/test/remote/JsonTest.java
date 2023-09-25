package com.raiden.http.core.springboot.test.remote;

import com.raiden.http.core.springboot.test.enitty.LoginBo;
import com.raiden.http.core.springboot.test.enitty.LoginEntity;
import com.raiden.http.core.springboot.test.enitty.ResponseEntity;
import com.raiden.tool.http.annotation.Body;
import com.raiden.tool.http.annotation.HttpServer;
import com.raiden.tool.http.annotation.RequireLine;
import com.raiden.tool.http.enums.HttpMethod;


/**
 * json测试
 *
 * @author fishlikewater@126.com
 * @since 2023年09月25日 11:35
 **/
@HttpServer(url = "192.168.5.225:8088/etc", sourceHttpClient = "third")
public interface JsonTest {


    @RequireLine(path = "/login", method = HttpMethod.POST)
    public ResponseEntity<LoginEntity> login(@Body LoginBo loginBo);

}

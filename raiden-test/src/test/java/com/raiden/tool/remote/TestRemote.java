package com.raiden.tool.remote;

import com.raiden.tool.http.MultipartData;
import com.raiden.tool.http.annotation.GET;
import com.raiden.tool.http.annotation.HttpServer;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;


/**
 * <p>
 *
 * </p>
 *
 * @author fishlikewater@126.com
 * @since 2023年09月22日 19:28
 **/

@HttpServer(url = "https://www.baidu.com")
public interface TestRemote {


    @GET
    String test();


    @GET("http://183.67.28.94:18887/dev/spots/image2/34/1.png")
    Path download(MultipartData multipartData);


    @GET("http://183.67.28.94:18887/dev/spots/image2/34/1.png")
    CompletableFuture<byte[]> download2();

}

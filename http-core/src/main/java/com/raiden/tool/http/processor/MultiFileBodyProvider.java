package com.raiden.tool.http.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.raiden.tool.http.file.MultipartData;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

/**
 * 文件上传处理器
 *
 * @author fishlikewater@126.com
 * @since 2023年09月27日 9:38
 **/
public class MultiFileBodyProvider implements HttpRequest.BodyPublisher{

    private final String boundary;

    private final long contentLength;

    private final byte[] paramByte;

    private final List<Path> paths;


    public MultiFileBodyProvider(MultipartData multipartData, Object paramObj){
        if (Objects.nonNull(multipartData.getPaths()) && multipartData.getPaths().length>0){
            paths = Arrays.stream(multipartData.getPaths()).map(Path::of).collect(Collectors.toList());
        }else {
            paths = Arrays.stream(multipartData.getFiles()).map(file -> Path.of(file.getPath())).collect(Collectors.toList());
        }
        this.boundary = boundaryString();
        StringBuilder paramData = new StringBuilder();
        if (Objects.nonNull(paramObj)){
            Map<String, Object> paramMap = BeanUtil.beanToMap(paramObj);
            paramMap.forEach((k, v)->{
                paramData.append("--").append(boundary).append("\r\n");
                paramData.append("Content-Disposition: form-data; name=\"").append(k).append("\"\r\n\r\n").append(v).append("\r\n");
            });
        }
        paramByte = paramData.toString().getBytes();
        contentLength = fileSize() + paramByte.length;
    }

    @Override
    public long contentLength() {
        return contentLength;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
        final List<ByteBuffer> paramBuf = copy(paramByte, paramByte.length);
        for (ByteBuffer byteBuffer : paramBuf) {
            subscriber.onNext(byteBuffer);
        }
        for (Path path : paths) {
            try {
                final String name = Files.getFileStore(path).name();
                String fileData = "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"file\"; filename=\"" + name + "\"\r\n" +
                        "Content-Type: application/octet-stream\r\n\r\n";
                final byte[] bytes = fileData.getBytes();
                final List<ByteBuffer> fileParam = copy(bytes,  bytes.length);
                for (ByteBuffer byteBuffer : fileParam) {
                    subscriber.onNext(byteBuffer);
                }
                //ByteBuffer buffer = ByteBuffer.allocate(8192); // 8KB缓冲区
                final File file = FileUtil.file(path.toFile());
                final FileReader fileReader = new FileReader(file);
                final byte[] readBytes = fileReader.readBytes();
                final ByteBuffer buffers = copy2(readBytes, readBytes.length);
                subscriber.onSubscribe(new Flow.Subscription() {
                    @Override
                    public void request(long n) {
                        subscriber.onNext(buffers);
                    }

                    @Override
                    public void cancel() {

                    }
                });
                subscriber.onComplete();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    ByteBuffer copy2(byte[] content, int length) {
        ByteBuffer b = ByteBuffer.allocate(length);
        b.put(content, 0, length);
        b.flip();
        return b;
    }

    List<ByteBuffer> copy(byte[] content, int length) {
        List<ByteBuffer> buffs = new ArrayList<>();
        ByteBuffer b = ByteBuffer.allocate(length);
        b.put(content, 0, length);
        b.flip();
        buffs.add(b);
        return buffs;
    }

    public long fileSize(){
        return paths.stream()
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .sum();
    }


    // 生成一个随机的boundary字符串
    private static String boundaryString() {
        return "----Boundary" + System.currentTimeMillis();
    }



}

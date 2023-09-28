package com.raiden.tool.http.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.raiden.tool.http.file.MultipartData;

import java.io.File;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;

/**
 * 文件上传处理器
 *
 * @author fishlikewater@126.com
 * @since 2023年09月27日 9:38
 **/
public class MultiFileBodyProvider implements HttpRequest.BodyPublisher {

    private final String boundary;

    private long contentLength;

    private final byte[] allBytes;


    public MultiFileBodyProvider(MultipartData multipartData, Object paramObj, String boundaryString) {
        List<Path> paths;
        if (Objects.nonNull(multipartData.getPaths()) && multipartData.getPaths().length > 0) {
            paths = Arrays.stream(multipartData.getPaths()).map(Path::of).collect(Collectors.toList());
        } else {
            paths = Arrays.stream(multipartData.getFiles()).map(file -> Path.of(file.getPath())).collect(Collectors.toList());
        }
        this.boundary = boundaryString;
        StringBuilder paramData = new StringBuilder();
        if (Objects.nonNull(paramObj)) {
            Map<String, Object> paramMap = BeanUtil.beanToMap(paramObj);
            paramMap.forEach((k, v) -> {
                paramData.append("--").append(boundary).append("\r\n");
                paramData.append("Content-Disposition: form-data; name=\"").append(k).append("\"\r\n\r\n").append(v).append("\r\n");
            });
        }
        byte[] paramByte = paramData.toString().getBytes();
        List<byte[]> byteList = new ArrayList<>();
        for (Path path : paths) {
            try {
                final File file = FileUtil.file(path.toFile());
                String fileData = "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
                        "Content-Type: application/octet-stream\r\n\r\n";
                final byte[] bytes = fileData.getBytes();
                final FileReader fileReader = new FileReader(file);
                final byte[] readBytes = fileReader.readBytes();
                byte[] fullData = new byte[readBytes.length + bytes.length];
                System.arraycopy(bytes, 0, fullData, 0, bytes.length);
                System.arraycopy(readBytes, 0, fullData, bytes.length, readBytes.length);
                byteList.add(fullData);
            } catch (Exception e) {
                throw new RuntimeException("构建文件数据异常", e);
            }

        }
        byte[] endString = ("\r\n--" + boundary + "--").getBytes();
        for (byte[] bytes : byteList) {
            contentLength += bytes.length;
        }
        contentLength += paramByte.length + endString.length;
        int flag = 0;
        allBytes = new byte[(int) contentLength];
        System.arraycopy(paramByte, 0, allBytes, flag, paramByte.length);
        flag = paramByte.length;
        for (byte[] bytes : byteList) {
            System.arraycopy(bytes, 0, allBytes, flag, bytes.length);
            flag += bytes.length;
        }
        System.arraycopy(endString, 0, allBytes, flag, endString.length);
    }

    @Override
    public long contentLength() {
        return contentLength;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
        final SubmissionPublisher<ByteBuffer> submissionPublisher = new SubmissionPublisher<>();
        submissionPublisher.subscribe(subscriber);
        final ByteBuffer endBuffer = copy2(allBytes, allBytes.length);
        submissionPublisher.submit(endBuffer);
        submissionPublisher.close();
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


}

package com.raiden.tool.http.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.raiden.tool.http.MultipartData;

import java.io.File;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
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
    private long contentLength = 0L;
    private final byte[] paramByte;
    private final byte[] endBytes;
    private final List<Path> paths;
    private final List<byte[]> fileParams = new ArrayList<>();


    public MultiFileBodyProvider(MultipartData multipartData, Object paramObj, String boundaryString) {
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
        paramByte = paramData.toString().getBytes();
        contentLength += paramByte.length;
        for (Path path : paths) {
            try {
                final File file = FileUtil.file(path.toFile());
                String fileData = "--" + boundary + "\r\n" +
                        "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n" +
                        "Content-Type: application/octet-stream\r\n\r\n";
                final byte[] bytes = fileData.getBytes();
                fileParams.add(bytes);
                contentLength += bytes.length;
                contentLength += file.length();
            } catch (Exception e) {
                throw new RuntimeException("构建文件数据异常", e);
            }
        }
        endBytes = ("\r\n--" + boundary + "--").getBytes();
        contentLength += endBytes.length;
    }

    @Override
    public long contentLength() {
        return contentLength;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super ByteBuffer> subscriber) {
        final SubmissionPublisher<ByteBuffer> submissionPublisher = new SubmissionPublisher<>();
        submissionPublisher.subscribe(subscriber);
        final ByteBuffer paramBuffer = copy2(paramByte, paramByte.length);
        submissionPublisher.submit(paramBuffer);
        int i = 0;
        for (Path path : paths) {
            final byte[] bytes = fileParams.get(i);
            submissionPublisher.submit(copy2(bytes, bytes.length));
            final File file = FileUtil.file(path.toFile());
            final FileReader fileReader = new FileReader(file);
            final byte[] readBytes = fileReader.readBytes();
            submissionPublisher.submit(copy2(readBytes, readBytes.length));
            i++;
        }
        final ByteBuffer endBuffer = copy2(endBytes, endBytes.length);
        submissionPublisher.submit(endBuffer);
        submissionPublisher.close();
    }

    ByteBuffer copy2(byte[] content, int length) {
        ByteBuffer b = ByteBuffer.allocate(length);
        b.put(content, 0, length);
        b.flip();
        return b;
    }
}

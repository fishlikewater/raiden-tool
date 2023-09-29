package com.raiden.tool.http;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.nio.file.OpenOption;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * 文件数据
 *
 * @author fishlikewater@126.com
 * @since 2023年09月26日 16:13
 **/
@Data
@Accessors(chain = true)
public class MultipartData {


    private String[] paths;

    private File[] files;

    private Path path;

    private OpenOption[] openOptions = new OpenOption[]{CREATE, WRITE};

    private MultipartData(String[] paths){this.paths = paths;}
    private MultipartData(File[] files){this.files = files;}
    private MultipartData(Path path, OpenOption...openOptions){
        this.path = path;
        this.openOptions = openOptions;
    }

    public static MultipartData ofFileUpload(String[] paths){
        return new MultipartData(paths);
    }

    public static MultipartData ofFileUpload(File[] files){
        return new MultipartData(files);
    }

    public static MultipartData ofFileDownload(Path path, OpenOption...openOptions){
        return new MultipartData(path, openOptions);
    }

}

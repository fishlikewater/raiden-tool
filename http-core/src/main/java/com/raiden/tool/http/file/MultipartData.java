package com.raiden.tool.http.file;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

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

    private MultipartData(String[] paths){this.paths = paths;}
    private MultipartData(File[] files){this.files = files;}

    public static MultipartData of(String[] paths){
        return new MultipartData(paths);
    }

    public static MultipartData of(File[] files){
        return new MultipartData(files);
    }

}

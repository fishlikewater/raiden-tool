package com.raiden.tool.core.utils;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 *
 * @since 2022年06月10日 14:28
 * @author fishlikewater@126.com
 * @version V1.0.0
 **/
@Getter
public enum ViewContentType {
    //文件类型
    DEFAULT("default","application/octet-stream"),
    JPG("jpg", "image/jpeg"),
    TIFF("tiff", "image/tiff"),
    GIF("gif", "image/gif"),
    JFIF("jfif", "image/jpeg"),
    PNG("png", "image/png"),
    TIF("tif", "image/tiff"),
    ICO("ico", "image/x-icon"),
    JPEG("jpeg", "image/jpeg"),
    WBMP("wbmp", "image/vnd.wap.wbmp"),
    FAX("fax", "image/fax"),
    NET("net", "image/pnetvue"),
    JPE("jpe", "image/jpeg"),
    RP("rp", "image/vnd.rn-realpix");

    private final String prefix;

    private final String type;

    public static String getContentType(String prefix){
        if(StrUtil.isEmpty(prefix)){
            return DEFAULT.getType();
        }
        prefix = prefix.substring(prefix.lastIndexOf(".") + 1);
        for (ViewContentType value : ViewContentType.values()) {
            if(prefix.equalsIgnoreCase(value.getPrefix())){
                return value.getType();
            }
        }
        return DEFAULT.getType();
    }

    ViewContentType(String prefix, String type) {
        this.prefix = prefix;
        this.type = type;
    }

}

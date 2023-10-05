package com.raiden.tool.core.base;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>
 *     返回基本结构
 * </p>
 * @author fishlikewater@126.com
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable{


    @Serial
    private static final long serialVersionUID = 1L;

    private final static CodeEnum DEFAULT_CODE = CodeEnum.SUCCESS;

    /** 返回状态码*/
    protected String code;

    /** 返回提示消息*/
    protected String message;

    /** 返回数据*/
    private T data;

    /** 请求唯一编号*/
    private String requestId;

    public Result() {
        setCode(DEFAULT_CODE);
        setMessage(DEFAULT_CODE.message());
        requestId = uuid();
    }

    public Result(T data) {
        this.data = data;
        setCode(DEFAULT_CODE);
        setMessage(DEFAULT_CODE.message());
        requestId = uuid();
    }
    public Result(CodeEnum code) {
        setCode(code);
        setMessage(code.message());
        requestId = uuid();
    }

    public Result(CodeEnum code, String message) {
        this.code = code.code();
        this.message = message;
        requestId = uuid();
    }

    public static <T> Result<T> of(T data, String message, CodeEnum code) {
        final Result<T> response = new Result<>(code, message);
        response.setData(data);
        return response;
    }

    public static <T> Result<T> of(T data, CodeEnum code) {
        final Result<T> response = new Result<>(code);
        response.setData(data);
        return response;
    }

    public static <T> Result<T> of(String message) {
        return new Result<>(DEFAULT_CODE, message);
    }


    /**
     * 设置响应消息
     *
     * @param code    请求状态码
     * @param message 消息
     * @param data  请求结果
     */
    public void setContent(CodeEnum code, String message, T data) {
        this.code = code.code();
        this.message = message;
        this.data = data;
        this.requestId = uuid();
    }

    /**
     * 生成UUID
     *
     * @return UUID
     */
    public String uuid() {
        final String requestId = RequestHolder.getRequestId();
        if (Objects.isNull(requestId)){
            return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
        }else {
            return requestId;
        }
    }

    public Result<T> setCode(CodeEnum code) {
        this.code = code.code();
        return this;
    }


}

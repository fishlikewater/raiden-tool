package com.raiden.tool.core.base;

import lombok.extern.slf4j.Slf4j;


/**
 * @author fishlikewater@126.com
 */
@Slf4j
public class BaseController {


    protected <T> Result<T> returnFail(String message){
        return returnEntity(null, message, CodeEnum.SYSTEM_ERROR);
    }

    protected <T> Result<T> returnSuccess(String message){
        return returnEntity(null, message, CodeEnum.SUCCESS);
    }

    protected <T> Result<T> returnSuccess(T obj){
        return returnEntity(obj, CodeEnum.SUCCESS.message(), CodeEnum.SUCCESS);
    }


    protected <T> Result<T> returnSuccess(T obj, String message){
        return returnEntity(obj, message, CodeEnum.SUCCESS);
    }

    protected <T> Result<T> returnEntity(String message, CodeEnum code){
        return returnEntity(null, message, code);
    }


    protected <T> Result<T> returnEntity(T obj, CodeEnum code){
        return returnEntity(obj, code.message(), code);
    }

    protected <T> Result<T> returnEntity(T obj, String message, CodeEnum code){
        return Result.of(obj, message, code);
    }
}

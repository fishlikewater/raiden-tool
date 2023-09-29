package com.raiden.tool.http.interceptor;

import com.raiden.tool.http.MethodArgsBean;

/**
 * 请求之前处理
 *
 * @author fishlikewater@126.com
 * @since 2023年09月26日 14:07
 **/
public interface PredRequest {

    void handler(MethodArgsBean methodArgsBean);

}

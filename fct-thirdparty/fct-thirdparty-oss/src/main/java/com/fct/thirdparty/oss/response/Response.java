package com.fct.thirdparty.oss.response;

import lombok.Data;

/**
 * Created by nick on 2017/5/17.
 */
@Data
public abstract class Response {

    /**
     * 返回码 0 成功 1000 失败
     */
    private int code;

    /**
     * 返回信息
     */
    private String msg;

}

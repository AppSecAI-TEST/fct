package com.fct.api.http.http.exceptions.common;


import com.fct.common.exceptions.BaseException;

/**
 * @author ningyang
 */
public class BadRequestException extends BaseException {

    public BadRequestException() {
        super(1001, "错误的请求格式", null);
    }
}

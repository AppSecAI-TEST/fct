package com.fct.web.pay.http.exceptions.common;


import com.fct.core.exceptions.BaseException;

/**
 * @author ningyang
 */
public class BadRequestException extends BaseException {

    public BadRequestException() {
        super(1001, "错误的请求格式", null);
    }
}

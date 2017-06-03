package com.fct.web.admin.http.exceptions.common;


import com.fct.common.exceptions.BaseException;

/**
 * @author ningyang
 */
public class RequestPostMissingKeyException extends BaseException {

    public RequestPostMissingKeyException() {
        super();
        this.code = 1000;
    }

    public RequestPostMissingKeyException(String keyName) {
        super();
        this.code = 1000;
        this.msg = String.format("[%s]字段不能为空", keyName);
    }
}

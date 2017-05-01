package com.fct.common.exceptions;

import org.apache.commons.lang3.StringUtils;

/**
 * @author ningyang
 */
public class BaseException extends RuntimeException {

    protected int code;
    protected String msg;
    protected String log;

    public BaseException() {
        this(1000, "业务操作过程中发生错误", null);
    }

    public BaseException(String msg) {
        this(1000, StringUtils.isEmpty(msg) ? "业务操作过程中发生错误" : msg, null);
    }

    public BaseException(int code, String msg) {
        this(code, msg, null);
    }

    public BaseException(int code, String msg, String log) {
        super();
        this.code = code;
        this.msg = msg;
        this.log = log;
    }

    public int code() {
        return this.code;
    }

    public String msg() {
        return this.msg;
    }

    public String log() {
        return this.log;
    }
}

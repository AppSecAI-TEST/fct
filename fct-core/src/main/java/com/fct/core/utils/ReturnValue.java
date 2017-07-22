package com.fct.core.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class ReturnValue<T> {

    private int code;
    private String msg;
    private T data;

    public ReturnValue() {
        this.code = 200;
        this.msg="success";
        this.data = null;
    }

    public ReturnValue(int code,String msg) {
        this.code = code;
        this.msg=msg;
        this.data = null;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}


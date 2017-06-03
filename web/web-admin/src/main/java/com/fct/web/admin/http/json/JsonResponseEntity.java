package com.fct.web.admin.http.json;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author ningyang
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonResponseEntity<T> {

    private int code;
    private String msg;
    private T data;

    public JsonResponseEntity() {
        this.code = 200;
        this.msg="success";
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

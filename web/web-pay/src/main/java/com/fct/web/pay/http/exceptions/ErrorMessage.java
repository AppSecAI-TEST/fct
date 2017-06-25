package com.fct.web.pay.http.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fct.core.exceptions.BaseException;
import lombok.Data;


/**
 * @author ningyang
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ErrorMessage {

    private int code = 1000;
    private String msg;

    public ErrorMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ErrorMessage(String msg) {
        this.msg = msg;
    }

    public ErrorMessage(BaseException ex) {
        this.code = ex.code();
        this.msg = ex.msg();
    }

}
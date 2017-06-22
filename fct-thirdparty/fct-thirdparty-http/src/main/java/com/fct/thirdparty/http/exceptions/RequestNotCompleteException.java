package com.fct.thirdparty.http.exceptions;

/**
 * Created by ningyang on 2017/5/13.
 */
public class RequestNotCompleteException extends RuntimeException {

    public RequestNotCompleteException() {

    }

    public RequestNotCompleteException(String msg){
        super(msg);
    }
}

package com.fct.thirdparty.http.exceptions;

/**
 * Created by ningyang on 2017/5/13.
 */
public class NotJsonResponseException extends HttpClientException {

    public NotJsonResponseException(Throwable throwable){

        super(throwable);
    }
}

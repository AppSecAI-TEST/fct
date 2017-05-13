package com.fct.thirdparty.http.exceptions;

/**
 * Created by ningyang on 2017/5/13.
 */
public class RequestParseException extends HttpClientException {

    public RequestParseException(){

    }

    public RequestParseException(Throwable cause){
        super(cause);
    }
}

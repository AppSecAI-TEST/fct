package com.fct.thirdparty.http.entity;

import com.fct.thirdparty.http.exceptions.ResponseParseException;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by ningyang on 2017/5/13.
 */
public abstract class AbstractResponseWrapper<T> implements ResponseWrapper<T> {

    protected Response response;
    protected Class<T> type;
    private String body;

    public AbstractResponseWrapper() {

    }

    @Override
    public void build(Response response) {
        this.response = response;
    }

    @Override
    public int code() {
        return response.code();
    }

    @Override
    public boolean isRead() {
        return body!=null;
    }

    @Override
    public String header(String name) {
        return response.header(name);
    }

    @Override
    public String body() {
        if(!isRead()) {
            try {
                body = response.body().string();
            } catch (IOException ex) {
                throw new ResponseParseException(ex);
            }
        }
        return body;
    }


    @Override
    public ResponseWrapper<T> withObjectType(Class<T> type) {
        this.type = type;
        return this;
    }

    @Override
    public Response nativeResponse() {
        return this.response;
    }
}

package com.fct.thirdparty.http;

import com.fct.thirdparty.http.callback.Callback;
import com.fct.thirdparty.http.entity.ResponseWrapper;
import com.fct.thirdparty.http.exceptions.ResponseParseException;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by ningyang on 2017/5/13.
 */
public class HttpRequestExecutor {

    private OkHttpClient client;
    private Request request;
    private Callback callback;
    private Response response;

    public HttpRequestExecutor(OkHttpClient client, Request request) {
        this.client = client;
        this.request = request;
    }

    public HttpRequestExecutor run() {
        try {
            if(callback == null) {
                response = client.newCall(request).execute();
            } else {
                client.newCall(request).enqueue(callback);
            }

            return this;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public ResponseWrapper as(Class<? extends ResponseWrapper> type) {
        try {
            ResponseWrapper wrapper = type.newInstance();
            if(callback!=null)
                wrapper.build(callback.getResponse());
            else
                wrapper.build(response);
            return wrapper;
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new ResponseParseException(ex);
        }
    }

    public HttpRequestExecutor callback(Callback callback) {
        this.callback = callback;
        return this;
    }
}

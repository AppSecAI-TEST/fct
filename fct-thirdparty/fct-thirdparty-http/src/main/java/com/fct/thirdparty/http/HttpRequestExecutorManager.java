package com.fct.thirdparty.http;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * Created by ningyang on 2017/5/13.
 */
public class HttpRequestExecutorManager {

    private final OkHttpClient client;

    public HttpRequestExecutorManager(OkHttpClient client) {
        this.client = client;
    }

    public HttpRequestExecutor newCall(Request request) {
        return new HttpRequestExecutor(this.client, request);
    }
}

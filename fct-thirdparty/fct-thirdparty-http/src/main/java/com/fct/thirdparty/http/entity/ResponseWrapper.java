package com.fct.thirdparty.http.entity;

import com.squareup.okhttp.Response;

/**
 * Created by ningyang on 2017/5/13.
 */
public interface ResponseWrapper<T> {

    void build(Response response);

    int code();

    boolean isRead();

    String header(String name);

    String body();

    T convertBody();

    ResponseWrapper<T> withObjectType(Class<T> type);

    Response nativeResponse();
}

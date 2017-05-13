package com.fct.thirdparty.http.entity;

/**
 * Created by ningyang on 2017/5/13.
 */
public class StringResponseWrapper extends AbstractResponseWrapper<String>{

    @Override
    public String convertBody() {
        return this.body();
    }
}

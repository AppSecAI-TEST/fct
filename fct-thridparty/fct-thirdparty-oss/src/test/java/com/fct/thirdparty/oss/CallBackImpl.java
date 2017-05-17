package com.fct.thirdparty.oss;

import com.fct.thirdparty.oss.callback.Callback;

import javax.sql.DataSource;

/**
 * Created by nick on 2017/5/17.
 */
public class CallBackImpl implements Callback {

    private DataSource dataSource;

    public CallBackImpl(DataSource dataSource){

    }

    @Override
    public Object callback() {
        return null;
    }
}

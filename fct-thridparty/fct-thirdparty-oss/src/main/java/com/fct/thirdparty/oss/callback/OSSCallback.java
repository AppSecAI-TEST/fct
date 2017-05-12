package com.fct.thirdparty.oss.callback;

/**
 * Created by nick on 2017/5/12.
 */
public class OSSCallback<T> {

    private Callback<T> callback;

    public OSSCallback(Callback callback){
        this.callback = callback;
    }

    public T callBack(){
        return callback.callback();
    }
}

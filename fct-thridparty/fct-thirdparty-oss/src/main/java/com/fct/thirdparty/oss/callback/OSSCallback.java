package com.fct.thirdparty.oss.callback;

import com.fct.thirdparty.oss.response.UploadResponse;

/**
 * Created by nick on 2017/5/12.
 */
public class OSSCallback<T> {

    private Callback<T> callback;

    public OSSCallback(Callback callback){
        this.callback = callback;
    }

    public T onSuccess(UploadResponse response){
        return callback.onSuccess(response);
    }


    public T onFailure(UploadResponse response){
        return callback.onFailure(response);
    }
}

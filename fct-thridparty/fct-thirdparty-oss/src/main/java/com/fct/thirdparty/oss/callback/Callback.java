package com.fct.thirdparty.oss.callback;

import com.fct.thirdparty.oss.response.UploadResponse;

/**
 * Created by ningyang on 2017/5/12.
 */
public interface Callback<T> {

    T onSuccess(UploadResponse response);

    T onFailure(UploadResponse response);
}

package com.fct.thirdparty.oss.callback;

import com.fct.thirdparty.oss.response.Response;

/**
 * Created by ningyang on 2017/5/12.
 */
public interface Callback<T> {

    T onSuccess(Response response);

    T onFailure(Response response);
}

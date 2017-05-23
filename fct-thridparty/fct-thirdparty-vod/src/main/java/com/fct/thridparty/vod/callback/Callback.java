package com.fct.thridparty.vod.callback;

import com.fct.thridparty.vod.response.VodResponse;

/**
 * Created by ningyang on 2017/5/23.
 */
public interface Callback {

    void onFail(VodResponse response);

    void onSuccess(VodResponse response);
}

package com.fct.thirdparty.http.callback;

import com.squareup.okhttp.Response;

/**
 * Created by ningyang on 2017/5/13.
 */
public interface Callback extends com.squareup.okhttp.Callback {

    /**
     * 封裝了okhttp的回調
     * 如果執行了回調則把返回的response塞到回調的子類當中
     * @return
     */
    Response getResponse();
}

package com.fct.thirdparty.oss.callback;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GenericRequest;
import com.fct.thirdparty.oss.response.Response;
import com.fct.thirdparty.oss.response.UploadResponse;

/**
 * Created by nick on 2017/5/17.
 */
public abstract class AbstractCallback<T> implements Callback<T>{

    protected OSSClient ossClient;

    protected GenericRequest request;

}

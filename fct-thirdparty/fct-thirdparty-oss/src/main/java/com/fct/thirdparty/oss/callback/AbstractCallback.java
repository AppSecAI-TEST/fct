package com.fct.thirdparty.oss.callback;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GenericRequest;

/**
 * Created by nick on 2017/5/17.
 */
public abstract class AbstractCallback<T> implements Callback<T>{

    protected OSSClient ossClient;

    protected GenericRequest request;

}

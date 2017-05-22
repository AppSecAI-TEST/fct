package com.fct.vod.test;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.AliyunVod;
import com.fct.thridparty.vod.RequestType;
import com.fct.thridparty.vod.response.VodResponse;
import com.google.common.collect.Maps;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.Dispatcher;
import com.squareup.okhttp.OkHttpClient;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by nick on 2017/5/19.
 */
public class VodTest {

    private HttpRequestExecutorManager manager;
    private String accessKeyId = "LTAI07UgXOHTbHd6";
    private String accessKeySecret = "j2PAwnos4tLfBXyOUzrF4bormfc3vt";

    @Before
    public void init(){
        OkHttpClient client = new OkHttpClient();
        ConnectionPool pool = new ConnectionPool(2000, 5 * 60 * 1000);
        client.setConnectionPool(pool);
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(64);
        dispatcher.setMaxRequestsPerHost(16);
        client.setDispatcher(dispatcher);
        client.setConnectTimeout(20, TimeUnit.SECONDS);
        client.setWriteTimeout(20, TimeUnit.SECONDS);
        client.setReadTimeout(20, TimeUnit.SECONDS);
        manager = new HttpRequestExecutorManager(client);
    }

    @Test
    public void vodTest(){
        Map<String, Object> selfParam = Maps.newHashMap();
        selfParam.putIfAbsent("VideoId", "212411251515");
        VodResponse response = new AliyunVod(manager, RequestType.GET, accessKeyId, accessKeySecret).
                                buildRequest().
                                withSelfParam(selfParam).
                                action("GetVideoInfo").
                                signature().
                                run().
                                response();
        System.out.println(response.getCode() + " : " + response.getMessage());
    }
}

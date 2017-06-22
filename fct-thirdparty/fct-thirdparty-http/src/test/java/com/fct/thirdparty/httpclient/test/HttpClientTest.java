package com.fct.thirdparty.httpclient.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thirdparty.http.builder.RequestBuilder;
import com.fct.thirdparty.http.entity.JsonNodeResponseWrapper;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.Dispatcher;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Created by ningyang on 2017/5/13.
 * http-client的單元測試
 */
public class HttpClientTest {

    private final static String url = "http://xxx.xxx";
    private String body = "{\"uid\":\"abcsdasdad\"}";

    @Test
    public void httpSender(){
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
        HttpRequestExecutorManager manager = new HttpRequestExecutorManager(client);
        Request request = new RequestBuilder().post().url(url).body(body).build();
        JsonNodeResponseWrapper wrapper = (JsonNodeResponseWrapper)manager.newCall(request).run().as(JsonNodeResponseWrapper.class);
        JsonNode node = wrapper.convertBody();
        Assert.assertNotNull(node);
    }
}

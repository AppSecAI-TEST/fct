package com.fct.vod.test;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.Action;
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

import static com.fct.thridparty.vod.Action.GetVideoInfo;
import static com.fct.thridparty.vod.Action.GetVideoPlayAuth;

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

    /**
     * 获取单个视频单元测试
     */
    @Test
    public void getVodTest(){
        Map<String, Object> selfParam = Maps.newHashMap();
        selfParam.putIfAbsent("VideoId", "212411251515");
        VodResponse response = new AliyunVod(manager, RequestType.GET, accessKeyId, accessKeySecret).
                                buildRequest().
                                withSelfParam(selfParam).
                                action(Action.GetVideoInfo).
                                signature().
                                run().
                                response();
        System.out.println(response.getCode() + " : " + response.getMessage());
    }

    /**
     * 获取播放凭证测试
     * 只需要在上一个单元测试基础上修改action
     */
    @Test
    public void getVodPlayAutTest(){
        Map<String, Object> selfParam = Maps.newHashMap();
        selfParam.putIfAbsent("VideoId", "212411251515");
        VodResponse response = new AliyunVod(manager, RequestType.GET, accessKeyId, accessKeySecret).
                buildRequest().
                withSelfParam(selfParam).
                action(Action.GetVideoPlayAuth).
                signature().
                run().
                response();
        System.out.println(response.getCode() + " : " + response.getMessage());
    }

    /**
     * 删除视频单元测试
     */
    @Test
    public void deleteVodTest(){
        Map<String, Object> selfParam = Maps.newHashMap();
        selfParam.putIfAbsent("VideoIds", "212411251515,212411251516");
        VodResponse response = new AliyunVod(manager, RequestType.DELETE, accessKeyId, accessKeySecret).
                                buildRequest().
                                withSelfParam(selfParam).
                                action(Action.DeleteVideo).
                                signature().
                                run().
                                response();
        System.out.println(response.getCode() + " : " + response.getMessage());
    }

    /**
     * 更新视频单元测试
     */
    @Test
    public void updateVodTest(){
        Map<String, Object> selfParam = Maps.newHashMap();
        selfParam.putIfAbsent("VideoId", "212411251515212411251516");
        selfParam.putIfAbsent("Title", "奔跑吧兄弟");
        selfParam.putIfAbsent("Description", "奔跑吧兄弟");
        selfParam.putIfAbsent("CoverURL", "http://img.test.com/212411251516.png");
        selfParam.putIfAbsent("CateId", "24");
        selfParam.putIfAbsent("Tags", "运动");
        VodResponse response = new AliyunVod(manager, RequestType.UPDATE, accessKeyId, accessKeySecret).
                buildRequest().
                withSelfParam(selfParam).
                action(Action.UpdateVideoInfo).
                signature().
                run().
                response();
        System.out.println(response.getCode() + " : " + response.getMessage());
    }


    /**
     * 获取视频列表
     */
    @Test
    public void getVideosTest(){
        Map<String, Object> selfParam = Maps.newHashMap();
        selfParam.putIfAbsent("Status", "Normal");
        selfParam.putIfAbsent("CateId", 12);
        selfParam.putIfAbsent("PageNo", 1);
        selfParam.putIfAbsent("PageSize", 20);
        selfParam.putIfAbsent("SortBy", "CreateTime:Desc");
        VodResponse response = new AliyunVod(manager, RequestType.GETLIST, accessKeyId, accessKeySecret).
                buildRequest().
                withSelfParam(selfParam).
                action(Action.GetVideoList).
                signature().
                run().
                response();
        System.out.println(response.getCode() + " : " + response.getMessage());
    }
}

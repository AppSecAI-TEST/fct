package com.fct.common.service.oss;

import com.aliyun.oss.OSSClient;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by nick on 2017/5/12.
 */
@Data
public class OSSClientFactory {

    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint;

    private final static Map<ClassLoader, OSSClient> holder = new ConcurrentHashMap<>();

    public OSSClientFactory(String endpoint, String accessKeyId, String accessKeySecret){
        this.endpoint = endpoint;
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    /**
     * 获取ossClient
     * @return
     */
    public OSSClient getOSSClient(){
        if(holder.get(getClassLoader())!=null){
            return holder.get(getClassLoader());
        }else{
            OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
            holder.putIfAbsent(getClassLoader(), ossClient);
            return ossClient;
        }
    }

    private ClassLoader getClassLoader(){
        return OSSClientFactory.class.getClassLoader();
    }
}

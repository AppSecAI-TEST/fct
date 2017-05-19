package com.fct.thridparty.vod.operator;

import com.fct.thirdparty.http.HttpRequestExecutorManager;
import com.fct.thridparty.vod.builder.VodAPIRequestBuilder;
import com.fct.thridparty.vod.request.VodAPIRequest;
import com.fct.thridparty.vod.response.VodResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by nick on 2017/5/19.
 */
public abstract class AbstractVodOperator implements VodOperator{

    protected VodAPIRequest vodAPIRequest;
    protected VodAPIRequestBuilder builder;
    protected VodResponse response;
    protected String accessKeySecret;
    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    protected static final String URL = "https://vod.cn-shanghai.aliyuncs.com";

    protected HttpRequestExecutorManager manager;

    protected Map<String, Object> selfParam;

    public abstract void buildRequest();

    public VodAPIRequest getRequest(){
        return vodAPIRequest;
    }
    /**
     * 把Date转换成ISO8601标准时间
     * @param date
     * @return
     */
    public String ISO8601TimeFormate(Date date){
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }

    public String[] getParams() throws UnsupportedEncodingException {
        Map<String, Object> objectMap = null;
        if(vodAPIRequest!=null){
            //循环向上转型
            for(Class clasz = vodAPIRequest.getClass(); clasz!=Object.class; clasz = clasz.getSuperclass()){
                Field[] fields = clasz.getDeclaredFields();
                objectMap = objectFieldToMap(fields, objectMap, vodAPIRequest, clasz);
            }
            Map<String, Object> sortedMap = Maps.newTreeMap(String::compareTo);
            sortedMap.putAll(objectMap);
            if(sortedMap.size()>0){
                List<String> list = Lists.newArrayList();
                Set<String> keys = sortedMap.keySet();
                Iterator<String> keyIt = keys.iterator();
                while(keyIt.hasNext()){
                    String key = keyIt.next();
                    if(sortedMap.get(key)!=null){
                        list.add(key);
                        list.add(sortedMap.get(key).toString());
                    }
                }
                int size = list.size();
                return list.toArray(new String[size]);
            }
        }
        return null;
    }

    private Map<String, Object> objectFieldToMap(Field[] fields, Map<String, Object> result, Object obj, Class<?> classz){
        try{
            for(Field field:fields){
                field.setAccessible(true);
                String fieldName = field.getName();
                String methodName = "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
                Method method = classz.getDeclaredMethod(methodName, null);//get方法不需要入参
                Object value = method.invoke(obj,null);
                if(result == null)
                    result = Maps.newHashMap();
                result.putIfAbsent(fieldName, value);
            }
            return result;
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}

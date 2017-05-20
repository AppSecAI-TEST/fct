package com.fct.thridparty.vod.utils;

import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by nick on 2017/5/20.
 * 反射工具
 */
public class ReflectionUtil {

    public static Map<String, Object> objectFieldToMap(Field[] fields, Map<String, Object> result, Object obj, Class<?> classz){
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

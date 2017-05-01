package com.fct.api.http.utils;

import java.lang.reflect.Method;

/**
 * @author ningyang
 */
public class ParamChecker {
	//构造实例
	public static ParamChecker builder(){
		return new ParamChecker();
	} 
	//检查枚举值
	public <T> ParamChecker checkEnumValue(Class<T> cs,String data){
		Method method;
		try {
			method = cs.getDeclaredMethod("fromValue",String.class);
			method.invoke(cs, data);
		} catch (Exception e) {
			throw new RuntimeException(e.getCause());
		}
		return this;
	}
	//检查枚举名称
	public <T> ParamChecker checkEnumName(Class<T> cs,String data){
		Method method;
		try {
			method = cs.getDeclaredMethod("fromName",String.class);
			method.invoke(cs, data);
		} catch (Exception e) {
			throw new RuntimeException(e.getCause());
		}
		return this;
	}
	//more check
}

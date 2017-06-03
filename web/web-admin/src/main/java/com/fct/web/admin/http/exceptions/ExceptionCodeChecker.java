package com.fct.web.admin.http.exceptions;

import com.fct.common.exceptions.BaseException;
import org.reflections.Reflections;

import java.util.Set;


/**
 * @author ningyang
 */
public class ExceptionCodeChecker {

    public static void check() throws Exception {
        Reflections reflections = new Reflections("com.fct");
        Set<Class<? extends BaseException>> classSet = reflections.getSubTypesOf(BaseException.class);
        for (Class<? extends BaseException> aClass : classSet) {
            BaseException ex = aClass.newInstance();
            System.out.print(ex.code() + "\t" + ex.msg() + "\t");
            System.out.println(aClass.getSimpleName());
        }
    }

}

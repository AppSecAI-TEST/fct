package com.fct.core.reflections;



import com.fct.core.exceptions.Exceptions;

import java.lang.reflect.Method;

/**
 * @author ningyang
 */
public class ReflectionInvoker {

    public static Object invoke(Object object, String methodName, Object[] args) {
        try {
            Class[] argsClasses = getArgsClasses(args);
            Method method = object.getClass().getDeclaredMethod(methodName, argsClasses);
            method.setAccessible(true);
            return method.invoke(object, args);
        } catch (Exception ex) {
            throw Exceptions.unchecked(ex);
        }
    }

    private static Class[] getArgsClasses(Object[] args) {
        Class[] argsClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsClasses[i] = args[i].getClass();
        }

        return argsClasses;
    }
}

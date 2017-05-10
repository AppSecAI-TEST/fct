package com.fct.api.web.http.support.version;

import com.fct.api.web.annotations.ParamIgnore;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * @author ningyang
 */
public class APIScanner {

    private static final String rootAPIPackage = "com.yimiao.vaccine.api.http.controller";

    public static List<String> getAPIsByExistAnnotation(Class annotation) {
        Preconditions.checkArgument(annotation.isAnnotation(), "参数必须是@interface!");

        List<String> result = Lists.newLinkedList();
        List<Class<?>> controllerClasses = Lists.newLinkedList();
        Reflections reflections = new Reflections(rootAPIPackage);
        controllerClasses.addAll(reflections.getTypesAnnotatedWith(Controller.class));
        controllerClasses.addAll(reflections.getTypesAnnotatedWith(RestController.class));

        for (Class clazz : controllerClasses) {
            RequestMapping classMappingInfo = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
            String rootUrl = classMappingInfo == null ? "" : removeEndSlash(classMappingInfo.value().length > 0 ? classMappingInfo.value()[0] : "");
            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isPublic(method.getModifiers()) && method.isAnnotationPresent(annotation)) {
                    RequestMapping mappingInfo = method.getAnnotation(RequestMapping.class);
                    Preconditions.checkArgument(mappingInfo.method().length < 2, "一个java方法只支持一种http请求方法");
                    for (int i = 0; i < mappingInfo.value().length; i++) {
                        String httpMethod = mappingInfo.method().length == 1 ? mappingInfo.method()[0].toString() : "GET";
                        String urlInfo = httpMethod + rootUrl + removeEndSlash(mappingInfo.value()[i]);
                        result.add(urlInfo);
                    }
                }
            }
        }
        return result;
    }


    public static List<String> getParamIgnoreAnnot(Class annotation) {

        Preconditions.checkArgument(annotation.isAnnotation(), "参数必须是@interface!");
        List<String> ignoreParams = Lists.newLinkedList();
        List<Class<?>> controllerClasses = Lists.newLinkedList();
        Reflections reflections = new Reflections(rootAPIPackage);
        controllerClasses.addAll(reflections.getTypesAnnotatedWith(Controller.class));
        controllerClasses.addAll(reflections.getTypesAnnotatedWith(RestController.class));

        for (Class clazz : controllerClasses) {
            for (Method method : clazz.getDeclaredMethods()) {
                if(Modifier.isPublic(method.getModifiers())) {
                    Annotation[][] annotations = method.getParameterAnnotations();
                    for(int i =0; i<annotations.length; i++){
                        Annotation[] annots = annotations[i];
                        for(Annotation annot: annots) {
                            if (annot.annotationType().isAssignableFrom(annotation)) {
                                ParamIgnore paramIgnore = (ParamIgnore) annot;
                                ignoreParams.add(paramIgnore.value());
                            }
                        }
                    }
                }
            }
        }
        return ignoreParams;
    }

    private static String removeEndSlash(String str) {
        if ("/".equals(str) || "".equals(str)) {//special case
            return "";
        }
        str = StringUtils.removeEnd(str, "/");
        return str.startsWith("/") ? str : ("/" + str);
    }
}

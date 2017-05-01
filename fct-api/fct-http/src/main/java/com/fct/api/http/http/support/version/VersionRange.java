package com.fct.api.http.http.support.version;

import java.lang.annotation.*;

/**
 * @author ningyang
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VersionRange {

    String from() default VersionConstant.min;

    String to() default VersionConstant.max;

    String[] exclude() default {};

    boolean deprecated() default false;
}

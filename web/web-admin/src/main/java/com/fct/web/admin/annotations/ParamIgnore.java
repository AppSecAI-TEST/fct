package com.fct.web.admin.annotations;

import java.lang.annotation.*;

/**
 *
 * @author ningyang
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamIgnore {

    String value() default "";
}

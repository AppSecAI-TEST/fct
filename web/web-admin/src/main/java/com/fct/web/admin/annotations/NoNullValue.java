package com.fct.web.admin.annotations;

import java.lang.annotation.*;

/**
 * Created by nick on 2017/6/5.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoNullValue {
}

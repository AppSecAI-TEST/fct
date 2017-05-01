package com.fct.api.http.annotations;

import java.lang.annotation.*;

/**
 **@author ningyang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WithoutToken {
}

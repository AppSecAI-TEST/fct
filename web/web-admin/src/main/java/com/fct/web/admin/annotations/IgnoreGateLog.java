package com.fct.web.admin.annotations;

import java.lang.annotation.*;

/**
 * @author ningyang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreGateLog {
}

package com.fct.api.web.http.support.session;

import java.lang.annotation.*;

/**
 * @author ningyang
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessToken {

    boolean guestEnabled() default false;
}

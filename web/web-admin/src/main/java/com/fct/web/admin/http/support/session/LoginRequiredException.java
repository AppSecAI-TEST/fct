package com.fct.web.admin.http.support.session;


import com.fct.common.exceptions.BaseException;

/**
 * @author ningyang
 */
public final class LoginRequiredException extends BaseException {

    public LoginRequiredException() {
        super(1000, "需要登录");
    }
}

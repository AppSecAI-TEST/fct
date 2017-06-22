package com.fct.web.admin.http.support.session;


import com.fct.core.exceptions.BaseException;

/**
 * @author ningyang
 */
public final class AccessTokenMissingException extends BaseException {

    public AccessTokenMissingException() {
        super(1000, "缺少access-token");
    }
}

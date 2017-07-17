package com.fct.api.web.http.controller;

import com.fct.api.web.utils.FctResourceUrl;
import com.fct.member.data.entity.MemberLogin;
import com.fct.member.interfaces.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by z on 17-6-28.
 */

public class BaseController {

    @Autowired
    public FctResourceUrl fctResourceUrl;

    @Autowired
    public MemberService memberService;

    protected String authToken;

    @ModelAttribute
    public void init(HttpServletRequest request, HttpServletResponse response) throws Exception {

        authToken = request.getHeader("auth-token");
    }

    public MemberLogin memberAuth()
    {
        if (StringUtils.isEmpty(this.authToken))
        {
            throw new IllegalArgumentException("登录授权已过期，请重新登录");
        }

        MemberLogin member = memberService.getMemberLogin(this.authToken);
        if (member == null)
        {
            throw new IllegalArgumentException("登录授权已过期，请重新登录");
        }

        return member;
    }

    public MemberLogin memberAuth(Boolean hasException)
    {
        if (StringUtils.isEmpty(this.authToken))
        {
            if (!hasException)
            {
                return null;
            }
            throw new IllegalArgumentException("非法请求");
        }

        return memberService.getMemberLogin(this.authToken);
    }

    protected String getImgUrl(String url)
    {
        return fctResourceUrl.getImageUrl(url);
    }

    protected List<String> getMutilImgUrl(String url)
    {
        return fctResourceUrl.getMutilImageUrl(url);
    }
}

package com.fct.api.web.http.controller;

import com.fct.api.web.config.FctConfig;
import com.fct.member.data.entity.MemberLogin;
import com.fct.member.interfaces.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by z on 17-6-28.
 */

public class BaseController {

    @Autowired
    private FctConfig fctConfig;

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
            throw new IllegalArgumentException("非法请求");
        }

        return memberService.getMemberLogin(this.authToken);
    }

    protected String getImgUrl(String url)
    {
        return String.format("%s%s",fctConfig.getImageUrl(),url);
    }

    protected List<String> getMutilImgUrl(String url)
    {
        if(StringUtils.isEmpty(url))
        {
            return new ArrayList<>();
        }
        String[] arrUrl = url.split(",");
        List<String> ls = new ArrayList<>();
        for (String img:arrUrl
             ) {
            ls.add(String.format("%s%s",fctConfig.getImageUrl(),img));
        }

        return ls;
    }
}

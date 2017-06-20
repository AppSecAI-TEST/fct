package com.fct.web.admin.http.controller;

import com.fct.member.data.entity.SysUserLogin;
import com.fct.member.data.entity.SystemUser;
import com.fct.web.admin.http.cache.CacheSysUserManager;
import com.fct.web.admin.utils.Constants;
import com.fct.web.admin.utils.CookieUtil;
import com.sun.jndi.toolkit.url.Uri;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

/**
 * Created by jon on 2017/6/9.
 */
public class BaseController {

    @Autowired
    private CacheSysUserManager cacheSysUserManager;

    public SysUserLogin currentUser;

    @ModelAttribute
    public void init(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        //获取cookie

        initUser(request,response);

        Constants constants =  new Constants();

        model.addAttribute("pub",constants);
        model.addAttribute("currentUser",currentUser);
    }

    void initUser(HttpServletRequest request, HttpServletResponse response)throws Exception
    {
        String token = CookieUtil.getCookieByName(request,"sysuser_token");
        if(StringUtils.isEmpty(token))
        {
            response.sendRedirect(request.getContextPath() + "/login?returnUrl="+request.getHeader("Referer")); // 跳到登录页面
            return;
        }
        currentUser = cacheSysUserManager.getSysUserLogin(token);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?returnUrl="+request.getHeader("Referer")); // 跳到登录页面
            return;
        }
    }

}

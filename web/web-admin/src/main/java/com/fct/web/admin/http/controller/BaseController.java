package com.fct.web.admin.http.controller;

import com.fct.member.data.entity.SysUserLogin;
import com.fct.web.admin.http.cache.CacheSysUserManager;
import com.fct.web.admin.utils.Constants;
import com.fct.core.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

        //currentUser = new SysUserLogin();

        Constants constants = new Constants();

        model.addAttribute("pub",constants);
        model.addAttribute("currentUser",currentUser);
    }

    void initUser(HttpServletRequest request, HttpServletResponse response)throws Exception
    {
        String token = CookieUtil.getCookieByName(request,"sysuser_token");
        String returnUrl = request.getHeader("Referer");
        returnUrl = !StringUtils.isEmpty(returnUrl) ? "?returnUrl="+returnUrl : "";
        if(StringUtils.isEmpty(token))
        {
            response.sendRedirect(request.getContextPath() + "/login"+returnUrl); // 跳到登录页面
            return;
        }
        currentUser = cacheSysUserManager.getSysUserLogin(token);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login"+returnUrl); // 跳到登录页面
            return;
        }
    }

}

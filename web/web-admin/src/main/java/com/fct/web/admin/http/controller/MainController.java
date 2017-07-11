package com.fct.web.admin.http.controller;

import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.*;
import com.fct.member.data.entity.SysUserLogin;
import com.fct.member.interfaces.MemberService;
import com.fct.web.admin.config.FctConfig;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.*;
import java.util.Enumeration;

@Controller
@RequestMapping(value = "")
public class MainController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private FctConfig fctConfig;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request,String returnurl,Model model) {

        model.addAttribute("returnurl",returnurl);
        model.addAttribute("pub",fctConfig);

        //model.addAttribute("password", StringHelper.md5("123456"));
        return "/main/login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request,HttpServletResponse response, Model model) {

        String token = CookieUtil.getCookieByName(request,"sysuser_token");

        CookieUtil.delCookie(request,response,"sysuser_token");
        try {
            if (!StringUtils.isEmpty(token)) {
                memberService.logoutSysUser(token);
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String error(String message,String returnurl,Model model) {

        return "/main/error";
    }


    @RequestMapping(value = "/main/verifylogin", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String verifylogin(HttpServletRequest request, HttpServletResponse response, String username,
                        String password, String returnurl) {

        username = ConvertUtils.toString(username);
        password =ConvertUtils.toString(password);

        try {

            SysUserLogin user = memberService.loginSystemUser(username,password, HttpUtils.getIp(request),
                    12);
            if(user !=null)
            {
                //写入cookie,测试暂时domain为空，不然写入cookie有问题，正式环境需传入 fangcun.com
                CookieUtil.addCookie(request,response,"sysuser_token",user.getToken());
            }
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            Constants.logger.error(Exceptions.getStackTraceAsString(exp));
            return AjaxUtil.alert("系统或网络错误，请稍候再试。");
        }
        if(StringUtils.isEmpty(returnurl))
        {
            returnurl = "/member";
        }
        return AjaxUtil.goUrl(returnurl,"登录成功。");
    }
}

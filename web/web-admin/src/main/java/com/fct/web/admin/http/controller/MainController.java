package com.fct.web.admin.http.controller;

import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.*;
import com.fct.member.data.entity.SysUserLogin;
import com.fct.member.data.entity.SystemUser;
import com.fct.member.interfaces.MemberService;
import com.fct.message.interfaces.MessageService;
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
import java.util.Date;

@Controller
@RequestMapping(value = "")
public class MainController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private FctConfig fctConfig;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request,String returnurl,Model model) {


        model.addAttribute("returnurl",returnurl);
        model.addAttribute("pub",fctConfig);

        long time = 0;
        String sendCodeTime = CookieUtil.getCookieByName(request,"sendCodeTime");
        if(!StringUtils.isEmpty(sendCodeTime)){
            time = DateUtils.compareDate(DateUtils.parseString(sendCodeTime),new Date());
            time = time /1000;
        }
        model.addAttribute("time", time);
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

    @RequestMapping(value = "/hm", method = RequestMethod.GET)
    public String hm(Model model) {

        model.addAttribute("pub",fctConfig);
        return "/main/hm";
    }

    @RequestMapping(value = "/main/sendcode", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String sendCode(HttpServletRequest request, HttpServletResponse response,String cellphone) {
        //String sessionId,String cellPhone,String content,String ip,String action
        //将sessionId写入cookie,1分钟有效;
        cellphone = ConvertUtils.toString(cellphone);

        String sessionId = UUIDUtil.generateUUID();
        if (StringUtils.isEmpty(cellphone) || !org.apache.commons.lang3.StringUtils.isNumeric(cellphone)
                || cellphone.length()!=11)
        {
            return AjaxUtil.alert("手机号码为空。");
        }

        try {

            SystemUser systemUser = memberService.getSystemUser(cellphone);
            if(systemUser == null)
            {
                return AjaxUtil.alert("手机号码不存在。");
            }

            CookieUtil.addCookie(request,response,"sessionId",sessionId,5);

            CookieUtil.addCookie(request,response,"sendCodeTime",
                    DateUtils.format(DateUtils.addMinute(new Date(),1)),1);

            messageService.sendVerifyCode(sessionId, cellphone, "{code}为您的登录验证码，5分钟内有效！",
                    HttpUtils.getIp(request), "syslogin");
        }
        catch (IllegalArgumentException exp)
        {
            AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            AjaxUtil.alert("系统出错或网络异常。");
        }

        return AjaxUtil.eval("time = 60;setInterval(ChangeTime, 1000);JQbox.alert('发送成功');");
    }


    @RequestMapping(value = "/main/verifylogin", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String verifylogin(HttpServletRequest request, HttpServletResponse response, String cellphone,
                        String password, String code,String returnurl) {

        cellphone = ConvertUtils.toString(cellphone);
        password =ConvertUtils.toString(password);
        code = ConvertUtils.toString(code);

        if(StringUtils.isEmpty(cellphone))
        {
            return AjaxUtil.alert("手机号码为空");
        }
        if(StringUtils.isEmpty(password))
        {
            return AjaxUtil.alert("密码为空");
        }
        if(StringUtils.isEmpty(code))
        {
            return AjaxUtil.alert("验证码为空。");
        }

        try {

            String sessionId = CookieUtil.getCookieByName(request,"sessionId");

            //String sessionId,String cellPhone,String code
            if(messageService.checkVerifyCode(sessionId,cellphone,code) == 0)
            {
                return AjaxUtil.alert("验证码不正确");
            }

            SysUserLogin user = memberService.loginSystemUser(cellphone,password, HttpUtils.getIp(request),
                    12);
            if(user !=null)
            {
                //写入cookie,测试暂时domain为空，不然写入cookie有问题，正式环境需传入 fangcun.com
                CookieUtil.addCookie(request,response,"sysuser_token",user.getToken(),720);
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
            returnurl = "/order";
        }
        return AjaxUtil.goUrl(returnurl,"登录成功。");
    }
}

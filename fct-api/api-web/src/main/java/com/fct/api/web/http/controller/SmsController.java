package com.fct.api.web.http.controller;

import com.fct.core.utils.ReturnValue;
import com.fct.message.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by z on 17-6-26.
 */
@RestController
@RequestMapping(value = "sms")
public class SmsController {

    @Autowired
    MessageService messageService;

    @RequestMapping(value = "send-captcha", method = RequestMethod.POST)
    public ReturnValue sendCaptcha(String cellphone, String session_id, String ip, String action) {

        String content = this.getContent(action);
        if (content == null) {
            return new ReturnValue(404, "请求来源错误");
        }

        messageService.sendVerifyCode(session_id,cellphone,content,
                ip,action);

        return new ReturnValue(200, "验证码发送成功");
    }

    protected String getContent(String action) {

        switch (action) {
            case "login":
                return "{code}为您的登录验证码，5分钟内有效！";
            case "forget":
                return "{code}为您的找回密码验证码，5分钟内有效！";
            case "bind":
                return "{code}为您的账号绑定验证码，5分钟内有效！";
            default:
                return null;

        }
    }
}

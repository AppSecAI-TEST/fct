package com.fct.api.web.http.controller;

import com.fct.core.utils.ReturnValue;
import com.fct.message.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
        if (content == "") {
            return new ReturnValue(404, "请求来源错误");
        }

        messageService.sendVerifyCode(session_id, cellphone, content, ip, action);

        return new ReturnValue(200, "验证码发送成功");
    }

    protected String getContent(String action) {

        Map<String, String> dict = new HashMap<>();
        dict.put("login", "快捷登录验证码：{code}");
        dict.put("register", "注册验证码：{code}");
        dict.put("forget_password", "找回密码验证码：{code}");

        return dict.containsKey(action) ? dict.get(action) : "";

    }
}

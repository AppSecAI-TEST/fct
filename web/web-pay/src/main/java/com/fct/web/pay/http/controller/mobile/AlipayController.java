package com.fct.web.pay.http.controller.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

@Controller("mobileAlipay")
@RequestMapping(value = "mobile/alipay")
public class AlipayController {

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public String callback() {
        return "mobile/alipay/callback";
    }
}

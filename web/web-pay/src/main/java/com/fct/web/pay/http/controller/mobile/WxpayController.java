package com.fct.web.pay.http.controller.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("mobileWxpay")
@RequestMapping(value = "mobile/wxpay")
public class WxpayController {

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public String notify(String returnurl,Model model) {

        model.addAttribute("returnurl",returnurl);
        return "/mobile/wxpay/notify";
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public String callback(String returnurl,Model model) {

        model.addAttribute("returnurl",returnurl);
        return "/mobile/wxpay/callback";
    }
}

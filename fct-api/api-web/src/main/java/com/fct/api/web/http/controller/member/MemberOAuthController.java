package com.fct.api.web.http.controller.member;

import com.fct.api.web.http.controller.BaseController;
import com.fct.common.interfaces.CommonService;
import com.fct.core.utils.ReturnValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by z on 17-7-12.
 */
@RestController
@RequestMapping(value = "oauth")
public class MemberOAuthController extends BaseController {

    @Autowired
    private CommonService commonService;

    /**获取微信授权地址
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<String> getOAuthUrl(String callbackURI) {

        String url = commonService.oauthUrl(callbackURI, "snsapi_userinfo");
        ReturnValue<String> response = new ReturnValue<>();
        response.setData(url);

        return response;
    }

    @RequestMapping(value = "callback", method = RequestMethod.GET)
    public ReturnValue callback(String code) {

        String openId = commonService.wechatCallback(code);


        return new ReturnValue(200, "微信回调成功");
    }

    /**用户与微信绑定
     *
     * @return
     */
    @RequestMapping(value = "bind", method = RequestMethod.POST)
    public ReturnValue bindMember() {


        return new ReturnValue(200, "绑定成功");
    }

}

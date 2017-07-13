package com.fct.api.web.http.controller.member;

import com.fct.api.web.http.controller.BaseController;
import com.fct.common.interfaces.CommonService;
import com.fct.common.interfaces.WeChatResponse;
import com.fct.common.interfaces.WeChatUserResponse;
import com.fct.core.utils.ReturnValue;
import com.fct.member.data.entity.MemberLogin;
import com.fct.message.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by z on 17-7-12.
 */
@RestController
@RequestMapping(value = "/member/oauth")
public class MemberOAuthController extends BaseController {

    @Autowired
    private CommonService commonService;

    @Autowired
    private MessageService messageService;

    /**获取微信授权地址
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<String> getOAuthUrl(String callback_url) {

        String url = commonService.oAuthURL(callback_url, "snsapi_userinfo");

        ReturnValue<String> response = new ReturnValue<>();
        response.setData(url);

        return response;
    }

    @RequestMapping(value = "callback", method = RequestMethod.GET)
    public ReturnValue<MemberLogin> callback(String code, String platform, String ip, Integer expire_day) {

        WeChatResponse weChat = commonService.wechatCallback(code);
        if (weChat == null) {
            return new ReturnValue(404, "授权失败");
        }

        MemberLogin member = this.memberAuth(false);

        Integer memberId = 0;
        String nickname = "";
        String headimgurl = "";
        Integer sex = 0;
        String unionid = "";

        if (member != null && member.getMemberId()>0) {

            WeChatUserResponse user = commonService.getUserInfo(weChat.getOpenid());
            memberId = member.getMemberId();
            nickname = user.getNickname();
            headimgurl =user.getHeadimgurl();
            sex = user.getSex();
            unionid = user.getUnionid();
        }

        member = memberService.saveMemberAuth(memberId, weChat.getOpenid(),
                platform, nickname, headimgurl, unionid, sex, ip, expire_day);

        ReturnValue<MemberLogin> response = new ReturnValue<>();
        response.setData(member);

        return response;
    }

    /**用户与微信绑定
     *
     * @return
     */
    @RequestMapping(value = "bind", method = RequestMethod.POST)
    public ReturnValue<MemberLogin> bindMember(String openid, String cellphone, String platform,
                                  String session_id, String captcha,
                                  String ip, Integer expire_day) {

        ReturnValue<MemberLogin> response = new ReturnValue<>();
        if (StringUtils.isEmpty(openid)) {
            response.setMsg("授权无效");
            response.setCode(404);

            return response;
        }

        if (messageService.checkVerifyCode(session_id, cellphone, captcha) <= 0) {
            response.setMsg("手机验证码不正确");
            response.setCode(404);

            return response;
        }

        WeChatUserResponse user = commonService.getUserInfo(openid);
        if (user == null) return new ReturnValue(404, "授权已失效，请重新授权");
         MemberLogin member = memberService.bindMemberAuth(cellphone, platform, openid, user.getNickname(),
                 user.getHeadimgurl(), user.getUnionid(), user.getSex(), ip, expire_day);

        response.setData(member);
        return response;
    }

}

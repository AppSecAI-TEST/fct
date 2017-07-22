package com.fct.api.web.http.controller.member;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.member.data.entity.MemberInfo;
import com.fct.member.data.entity.MemberLogin;
import com.fct.message.interfaces.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/** 用户类
 *
 */
@RestController
@RequestMapping(value = "/member")
public class MemberController extends BaseController {


    @Autowired
    private MessageService messageService;

    /**用户登录与快捷登录（注册）
     *
     * @param cellphone
     * @param password
     * @param session_id
     * @param captcha
     * @param ip
     * @param expire_day
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ReturnValue<MemberLogin> login(String cellphone, String password, String platform,
                                          String session_id, String captcha,
                                          String ip, Integer expire_day) {

        if (StringUtils.isEmpty(cellphone) || cellphone.length() != 11)
            return new ReturnValue<>(404, "请输入正确的手机号");

        ReturnValue<MemberLogin> response = new ReturnValue<>();
        if (session_id.length() > 0 && captcha.length() > 0) {
            password = "";
            if (messageService.checkVerifyCode(session_id, cellphone, captcha) <= 0) {
                response.setMsg("手机验证码不正确");
                response.setCode(404);

                return response;
            }
        } else if (password.isEmpty()) {

            response.setMsg("请输入密码");
            response.setCode(404);

            return  response;
        }

        MemberLogin member = memberService.loginMember(cellphone, password, platform, ip, expire_day);
        if (StringUtils.isEmpty(member.getHeadPortrait())) {
            member.setHeadPortrait(fctResourceUrl.getImageUrl("/static/img/head.jpg"));
        } else {
            member.setHeadPortrait(fctResourceUrl.getImageUrl(member.getHeadPortrait()));
        }
        response.setData(member);

        return  response;
    }

    /**用户更新
     *
     * @param username
     * @param gender
     * @param weixin
     * @return
     */
    @RequestMapping(value = "update-info", method = RequestMethod.POST)
    public ReturnValue updateInfo(String username, String avatar, Integer gender, String birthday, String weixin) {

        avatar = ConvertUtils.toString(avatar);
        username = ConvertUtils.toString(username);
        gender = ConvertUtils.toInteger(gender, 0);
        birthday = ConvertUtils.toString(birthday);
        weixin = ConvertUtils.toString(weixin);

        MemberLogin member = this.memberAuth();
        memberService.updateMemberInfo(member.getMemberId(), avatar, username, gender, birthday, weixin);

        return new ReturnValue<>(200, "修改成功");
    }

    /**修改密码
     *
     * @param old_password
     * @param new_password
     * @return
     */
    @RequestMapping(value = "change-password", method = RequestMethod.POST)
    public ReturnValue changePassword(String old_password, String new_password) {

        MemberLogin member = this.memberAuth();
        memberService.updateMemberPassword(member.getMemberId(), old_password, new_password, new_password);
        return new ReturnValue();
    }

    /**找回密码
     *
     * @param cellphone
     * @param password
     * @param session_id
     * @param captcha
     * @return
     */
    @RequestMapping(value = "forget-password", method = RequestMethod.POST)
    public ReturnValue forgetPassword(String cellphone, String password,
                                      String session_id, String captcha) {

        if (messageService.checkVerifyCode(session_id, cellphone, captcha) <= 0) {

            return new ReturnValue(404,"手机验证码不正确");
        }

        memberService.forgetPassword(cellphone, password);

        return  new ReturnValue();
    }

    /**绑定银行卡与实名认证
     *
     * @param name
     * @param idcard_no
     * @param idcard_image_url
     * @param bank_name
     * @param bank_account
     * @return
     */
    @RequestMapping(value = "real-auth", method = RequestMethod.POST)
    public ReturnValue realAuth(String name,
                                String idcard_no, String idcard_image_url,
                                String bank_name, String bank_account) {

        MemberLogin member = this.memberAuth();
        memberService.authenticationMember(member.getMemberId(), name,
                idcard_no,idcard_image_url, bank_name, bank_account);

        return new ReturnValue();
    }

    @RequestMapping(value = "info", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> getMemberInfo() {

        MemberLogin member = this.memberAuth();

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", member.getMemberId());
        map.put("cellPhone", member.getCellPhone());
        map.put("userName", member.getUserName());
        map.put("headPortrait", StringUtils.isEmpty(member.getHeadPortrait())
                ? "" : fctResourceUrl.getImageUrl(member.getHeadPortrait()));


        MemberInfo info = memberService.getMemberInfo(member.getMemberId());
        if (info != null) {

            map.put("sex", info.getSex());
            map.put("birthday", info.getBirthday());
            map.put("weixin", info.getWeixin());
        }

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(map);

        return response;
    }

    @RequestMapping(value = "get-by-token", method = RequestMethod.GET)
    public ReturnValue<MemberLogin> getByToken() {
        MemberLogin member = this.memberAuth();

        ReturnValue<MemberLogin> response = new ReturnValue<>();
        response.setData(member);

        return response;
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ReturnValue logout() {

        return new ReturnValue();
    }
}

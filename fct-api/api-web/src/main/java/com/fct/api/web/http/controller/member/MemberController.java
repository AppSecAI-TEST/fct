package com.fct.api.web.http.controller.member;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ReturnValue;
import com.fct.member.data.entity.MemberInfo;
import com.fct.member.data.entity.MemberLogin;
import com.fct.member.interfaces.MemberService;
import com.fct.message.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


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
    public ReturnValue<MemberLogin> login(String cellphone, String password,
                                          String session_id, String captcha,
                                          String ip, Integer expire_day) {

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

        MemberLogin member = memberService.loginMember(cellphone, password, ip, expire_day);
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
    public ReturnValue updateInfo(String username, Integer gender, String weixin) {

        MemberLogin member = this.memberAuth();
        MemberInfo memberInfo = memberService.getMemberInfo(member.getMemberId());
        memberInfo.setSex(gender);
        memberInfo.setWeixin(weixin);
        memberService.updateMemberInfo(memberInfo);

        return new ReturnValue<>();
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

    @RequestMapping(value = "get-by-token", method = RequestMethod.GET)
    public ReturnValue<MemberLogin> getByToken()
    {
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

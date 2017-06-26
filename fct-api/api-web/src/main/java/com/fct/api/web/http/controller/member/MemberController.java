package com.fct.api.web.http.controller.member;

import com.fct.core.utils.ReturnValue;
import com.fct.member.data.entity.MemberBankInfo;
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
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MessageService messageService;

    /**用户登录与快捷登录（注册）
     *
     * @param cellphone
     * @param password
     * @param sessionId
     * @param captcha
     * @param ip
     * @param expireDay
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ReturnValue<MemberLogin> login(String cellphone, String password,
                                          String sessionId, String captcha,
                                          String ip, Integer expireDay) {

        ReturnValue<MemberLogin> response = new ReturnValue<>();
        if (sessionId.length() > 0 && captcha.length() > 0) {
            password = "";
            if (messageService.checkVerifyCode(sessionId, cellphone, captcha) <= 0) {
                response.setMsg("手机验证码不正确");
                response.setCode(404);

                return response;
            }
        } else if (password.isEmpty()) {

            response.setMsg("请输入密码");
            response.setCode(404);

            return  response;
        }

        MemberLogin member = memberService.loginMember(cellphone, password, ip, expireDay);
        response.setData(member);

        return  response;
    }

    /**用户更新
     *
     * @param memberId
     * @param username
     * @param gender
     * @param weixin
     * @return
     */
    @RequestMapping(value = "update-info", method = RequestMethod.POST)
    public ReturnValue updateInfo(Integer memberId, String username, Integer gender, String weixin) {

        MemberInfo memberInfo = memberService.getMemberInfo(memberId);
        memberInfo.setSex(gender);
        memberInfo.setWeixin(weixin);
        memberService.updateMemberInfo(memberInfo);

        return new ReturnValue<>();
    }

    /**修改密码
     *
     * @param memberId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "change-password", method = RequestMethod.POST)
    public ReturnValue changePassword(Integer memberId, String oldPassword, String newPassword) {

        memberService.updateMemberPassword(memberId, oldPassword, newPassword, newPassword);
        return new ReturnValue();
    }

    /**找回密码
     *
     * @param memberId
     * @param cellphone
     * @param password
     * @param sessionId
     * @param captcha
     * @return
     */
    @RequestMapping(value = "forget-password", method = RequestMethod.POST)
    public ReturnValue forgetPassword(Integer memberId, String cellphone,
                                                   String password, String sessionId, String captcha) {

        if (messageService.checkVerifyCode(sessionId, cellphone, captcha) <= 0) {

            return new ReturnValue(404,"手机验证码不正确");
        }

        memberService.forgetPassword(cellphone, password);

        return  new ReturnValue();
    }

    /**绑定银行卡与实名认证
     *
     * @param memberId
     * @param cellphone
     * @param name
     * @param bankName
     * @param bankAccount
     * @return
     */
    @RequestMapping(value = "real-auth", method = RequestMethod.POST)
    public ReturnValue realAuth(Integer memberId, String cellphone,
                                                    String name, String bankName, String bankAccount) {

        MemberBankInfo bankInfo = memberService.getMemberBankInfo(memberId);
        bankInfo.setCellPhone(cellphone);
        bankInfo.setName(name);
        bankInfo.setBankName(bankName);
        bankInfo.setBankAccount(bankAccount);
        memberService.saveMemberBankInfo(bankInfo);

        return new ReturnValue();
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ReturnValue logout(Integer memberId) {

        return new ReturnValue();
    }
}

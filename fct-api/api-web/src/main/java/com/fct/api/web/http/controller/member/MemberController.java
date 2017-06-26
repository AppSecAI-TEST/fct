package com.fct.api.web.http.controller.member;

import com.fct.api.web.http.json.JsonResponseEntity;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.MemberBankInfo;
import com.fct.member.data.entity.MemberInfo;
import com.fct.member.data.entity.MemberLogin;
import com.fct.member.interfaces.MemberService;
import com.fct.message.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Null;


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
    public JsonResponseEntity<MemberLogin> login(String cellphone, String password,
                                                 String sessionId, String captcha,
                                                 String ip, Integer expireDay) {

        JsonResponseEntity<MemberLogin> response = new JsonResponseEntity<>();
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
    public JsonResponseEntity<String> updateInfo(Integer memberId, String username, Integer gender, String weixin) {

        MemberInfo memberInfo = memberService.getMemberInfo(memberId);
        memberInfo.setSex(gender);
        memberInfo.setWeixin(weixin);
        memberService.updateMemberInfo(memberInfo);

        JsonResponseEntity<String> response = new JsonResponseEntity<>();

        return  response;
    }

    /**修改密码
     *
     * @param memberId
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "change-password", method = RequestMethod.POST)
    public JsonResponseEntity<String> changePassword(Integer memberId, String oldPassword, String newPassword) {

        memberService.updateMemberPassword(memberId, oldPassword, newPassword, newPassword);
        JsonResponseEntity<String> response = new JsonResponseEntity<>();

        return  response;
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
    public JsonResponseEntity<String> forgetPassword(Integer memberId, String cellphone,
                                                   String password, String sessionId, String captcha) {

        JsonResponseEntity<String> response = new JsonResponseEntity<>();
        if (messageService.checkVerifyCode(sessionId, cellphone, captcha) <= 0) {
            response.setMsg("手机验证码不正确");
            response.setCode(404);

            return response;
        }

        memberService.forgetPassword(cellphone, password);

        return  response;
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
    public JsonResponseEntity<String> realAuth(Integer memberId, String cellphone,
                                                    String name, String bankName, String bankAccount) {

        MemberBankInfo bankInfo = memberService.getMemberBankInfo(memberId);
        bankInfo.setCellPhone(cellphone);
        bankInfo.setName(name);
        bankInfo.setBankName(bankName);
        bankInfo.setBankAccount(bankAccount);
        memberService.saveMemberBankInfo(bankInfo);

        JsonResponseEntity<String> response = new JsonResponseEntity<>();

        return  response;
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public JsonResponseEntity<String> logout(Integer memberId) {

        JsonResponseEntity<String> response = new JsonResponseEntity<>();

        return  response;
    }
}

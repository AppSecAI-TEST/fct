package com.fct.api.web.http.controller.member;

import com.fct.api.web.http.json.JsonResponseEntity;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.MemberLogin;
import com.fct.member.interfaces.MemberService;
import com.fct.message.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by jon on 2017/5/10.
 * I love nancy, and I want to marry you
 */
@RestController
@RequestMapping(value = "/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public JsonResponseEntity<Member> register(@RequestParam String cellphone,
                                               String username,String password){

        Member member = memberService.registerMember(cellphone,username,password);

        JsonResponseEntity<Member> responseEntity = new JsonResponseEntity<>();
        responseEntity.setData(member);
        return responseEntity;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public JsonResponseEntity<MemberLogin> login(String cellphone,String password,
                                                 String ip,Integer expireday){

        JsonResponseEntity<MemberLogin> response = new JsonResponseEntity<>();
        MemberLogin member = memberService.loginMember(cellphone, password, ip, expireday);
        response.setData(member);
        return response;
    }

    @RequestMapping(value = "getlogin", method = RequestMethod.GET)
    public JsonResponseEntity<MemberLogin> getLogin(String token){

        JsonResponseEntity<MemberLogin> response = new JsonResponseEntity<>();

        MemberLogin member = memberService.getMemberLogin(token);
        response.setData(member);
        return response;
    }

    @RequestMapping(value = "quicklogin", method = RequestMethod.POST)
    public JsonResponseEntity<MemberLogin> quickLogin(String cellphone,String seesionid,String validcode,
                                                 String ip,Integer expireday){

        JsonResponseEntity<MemberLogin> response = new JsonResponseEntity<>();

        if(messageService.checkVerifyCode(seesionid,cellphone,validcode)<=0)
        {
            response.setMsg("验证码不正确");
            response.setCode(404);
        }
        else {
            MemberLogin member = memberService.loginMember(cellphone, "", ip, expireday);
            response.setData(member);
        }

        return response;
    }
}

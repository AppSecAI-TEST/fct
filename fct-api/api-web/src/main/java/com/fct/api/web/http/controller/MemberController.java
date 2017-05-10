package com.fct.api.web.http.controller;

import com.fct.api.web.http.json.JsonResponseEntity;
import com.fct.member.data.entity.Member;
import com.fct.member.interfaces.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public JsonResponseEntity<Member> register(@RequestParam String cellphone,
                                               String username,String password){

        Member member = memberService.registerMember(cellphone,username,password);

        JsonResponseEntity<Member> responseEntity = new JsonResponseEntity<>();
        responseEntity.setData(member);
        return responseEntity;
    }

    @RequestMapping(value = "getmember", method = RequestMethod.GET)
    public JsonResponseEntity<Member> getMember(@RequestParam Integer id){

        Member member = memberService.getMember(id);

        JsonResponseEntity<Member> responseEntity = new JsonResponseEntity<>();

        return responseEntity;
    }
}

package com.fct.web.pay.http.cache;

import com.fct.member.data.entity.MemberLogin;
import com.fct.member.interfaces.MemberService;
import com.fct.web.pay.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheMemberManager {

    @Autowired
    private MemberService memberService;

    public MemberLogin getMemberLogin(String token) {
        try
        {
            return memberService.getMemberLogin(token);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }
}

package com.fct.web.admin.http.cache;

import com.fct.member.data.entity.SysUserLogin;
import com.fct.member.interfaces.MemberService;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheSysUserManager {
    @Autowired
    private MemberService memberService;

    public SysUserLogin getSysUserLogin(String token) {
        try
        {
            return memberService.getSysUserLogin(token);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }
}

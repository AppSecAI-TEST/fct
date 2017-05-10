package com.fct.member.service.business;

import com.fct.common.utils.DateUtils;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.MemberInfo;
import com.fct.member.data.entity.MemberLogin;
import com.fct.member.data.entity.MemberStore;
import com.fct.member.data.repository.MemberLoginRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * Created by jon on 2017/5/6.
 */
@Service
public class MemberLoginManager {

    @Autowired
    private MemberLoginRepository memberLoginRepository;


    public MemberLogin login(String cellPhone, String password,String ip,Integer expireDay)
    {
        if(StringUtils.isEmpty(cellPhone))
        {
            throw new IllegalArgumentException("手机号码为空。");
        }
        //普通登陆，用户名+密码
        Member member =  MemberManager.getInstance().login(cellPhone,password);
        if(member == null)
        {
            throw new IllegalArgumentException("用户户或密码错误。");
        }

        MemberInfo info = MemberInfoManager.getInstance().findById(member.getId());

        MemberStore store = MemberStoreManager.getInstance().findByMemberId(member.getId());

        MemberLogin login = new MemberLogin();
        login.setCellPhone(cellPhone);
        login.setMemberId(member.getId());
        login.setExpireTime(DateUtils.addDay(new Date(),expireDay));
        login.setHeadPortrait(info.getHeadPortrait());
        login.setInviterId(member.getInviterMemberId());
        login.setIp(ip);
        login.setAuthStatus(member.getAuthStatus());
        login.setLoginTime(new Date());
        login.setToken(UUID.randomUUID().toString());
        login.setUserName(member.getUserName());
        login.setShopId(store!=null ?store.getId() :0);
        login.setGradeId(member.getGradeId());

        memberLoginRepository.save(login);

        return login;
    }
}

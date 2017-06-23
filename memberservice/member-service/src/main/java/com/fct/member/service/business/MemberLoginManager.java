package com.fct.member.service.business;

import com.fct.core.utils.DateUtils;
import com.fct.member.data.entity.*;
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

    @Autowired
    private MemberManager memberManager;

    @Autowired
    private MemberInfoManager memberInfoManager;

    @Autowired
    private MemberStoreManager memberStoreManager;


    public MemberLogin login(String cellPhone, String password,String ip,Integer expireDay)
    {
        if(StringUtils.isEmpty(cellPhone))
        {
            throw new IllegalArgumentException("手机号码为空。");
        }
        //普通登陆，用户名+密码
        Member member =  memberManager.login(cellPhone,password);
        if(member == null)
        {
            throw new IllegalArgumentException("用户户或密码错误。");
        }

        return login(member,ip,expireDay);
    }

    private MemberLogin login(Member member,String ip,Integer expireDay)
    {
        if(member.getLocked() ==1)
        {
            throw new IllegalArgumentException("用户被锁，请联系管理员。");
        }
        MemberInfo info = memberInfoManager.findById(member.getId());

        MemberStore store = memberStoreManager.findByMemberId(member.getId());

        MemberLogin login = new MemberLogin();
        login.setCellPhone(member.getCellPhone());
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

    public MemberLogin quickLogin(String cellPhone,String ip,Integer expireDay)
    {
        if(StringUtils.isEmpty(cellPhone))
        {
            throw new IllegalArgumentException("手机号码为空。");
        }
        Member member = memberManager.findByCellPhone(cellPhone);


        return login(member,ip,expireDay);
    }

    public void logOut(String token)
    {
        if(StringUtils.isEmpty(token))
        {
            throw new IllegalArgumentException("token为空。");
        }
        MemberLogin login = memberLoginRepository.findOne(token);
        login.setExpireTime(DateUtils.addDay(new Date(),-1));
        memberLoginRepository.save(login);
    }

    public MemberLogin findByToken(String token)
    {
        if(StringUtils.isEmpty(token))
        {
            throw new IllegalArgumentException("token为空。");
        }
        MemberLogin login =  memberLoginRepository.findOne(token);
        if(DateUtils.compareDate(login.getExpireTime(),new Date())<0)
        {
            return  null;
        }
        return login;
    }
}

package com.fct.member.service.business;

import com.fct.core.json.JsonConverter;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.UUIDUtil;
import com.fct.member.data.entity.*;
import com.fct.member.data.repository.MemberLoginRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    @Autowired
    private MemberAuthManager memberAuthManager;

    @Autowired
    private JdbcTemplate jt;

    public MemberLogin login(String cellPhone, String password,String platform,String ip,Integer expireDay)
    {
        //普通登陆，用户名+密码
        Member member =  memberManager.login(cellPhone,password);
        if(member == null)
        {
            throw new IllegalArgumentException("用户名或密码错误。");
        }

        return login(member,platform,memberAuthManager.getOpenId(member.getId(),platform),ip,expireDay);
    }

    public MemberLogin login(Member member,String platform,String openId,String ip,Integer expireDay)
    {
        if(StringUtils.isEmpty(ip))
        {
            throw new IllegalArgumentException("ip为空。");
        }
        if(expireDay<=0)
        {
            throw new IllegalArgumentException("过期时间为空。");
        }
        if(member.getLocked() ==1)
        {
            throw new IllegalArgumentException("用户被锁，请联系管理员。");
        }
        MemberInfo info = memberInfoManager.findById(member.getId());

        MemberStore store = memberStoreManager.findByMemberId(member.getId());

        member.setLoginCount(member.getLoginCount()+1);
        member.setLoginTime(new Date());
        memberManager.save(member);

        Constants.logger.info("login。。。");

        MemberLogin login = new MemberLogin();
        login.setCellPhone(member.getCellPhone());
        login.setMemberId(member.getId());
        login.setExpireTime(DateUtils.addDay(new Date(),expireDay));
        login.setHeadPortrait(info.getHeadPortrait());
        login.setInviterId(member.getInviterMemberId());
        login.setIp(ip);
        login.setAuthStatus(member.getAuthStatus());
        login.setLoginTime(new Date());
        login.setToken(UUIDUtil.generateUUID());
        login.setUserName(member.getUserName());
        login.setShopId(store!=null&&store.getStatus()==1 ?store.getId() :0);
        login.setGradeId(member.getGradeId());
        login.setLoginPlatform(platform);
        login.setOpenId(openId);
        login.setLoginCount(member.getLoginCount());

        Constants.logger.info("loginData:"+ JsonConverter.toJson(login));

        memberLoginRepository.save(login);

        Constants.logger.info("login yeah。。。");

        return login;
    }

    public MemberLogin quickLogin(String cellPhone,String platform,String ip,Integer expireDay)
    {
        Member member = memberManager.findByCellPhone(cellPhone);
        if(member ==  null)
        {
            member = memberManager.register(cellPhone,cellPhone,cellPhone.substring(5),"",1);
        }
        return login(member,platform,memberAuthManager.getOpenId(member.getId(),platform),ip,expireDay);
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

    public void updateLogin(String token,Member member,MemberInfo info)
    {
        MemberLogin  login = findByToken(token);
        if(login.getMemberId() != member.getId())
        {
            throw new IllegalArgumentException("非法操作。");
        }
        login.setHeadPortrait(info.getHeadPortrait());
        login.setUserName(member.getUserName());

        memberLoginRepository.save(login);
    }

    public void updateAudiStatus(Integer memberId,Integer audiStatus)
    {
        String sql = String.format("update MemberLogin set authStatus=%d where memberId=%d and expireTime>'%s'",
                audiStatus,memberId,DateUtils.format(new Date()));
        jt.update(sql);
    }
}

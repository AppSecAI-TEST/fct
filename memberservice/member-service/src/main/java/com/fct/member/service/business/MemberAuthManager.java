package com.fct.member.service.business;

import com.fct.member.data.entity.*;
import com.fct.member.data.repository.MemberAuthRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/8.
 */
@Service
public class MemberAuthManager {

    @Autowired
    MemberAuthRepository memberAuthRepository;

    @Autowired
    private MemberManager memberManager;

    @Autowired
    private MemberInfoManager memberInfoManager;

    @Autowired
    private MemberLoginManager loginManager;

    @Autowired
    private JdbcTemplate jt;

    public MemberAuth save(MemberAuth auth)
    {

        return memberAuthRepository.saveAndFlush(auth);
    }

    public String getOpenId(Integer memberId,String platform) {

        if(memberId <=0 || StringUtils.isEmpty(platform))
        {
            return "";
        }
        MemberAuth auth = memberAuthRepository.findByMemberIdAndPlatform(memberId,platform);
        if(auth != null)
            return auth.getOpenId();

        return "";
    }

    @Transactional
    public MemberLogin bind(String cellPhone,String platform,String openId, String nickName,String headImgUrl,
                            String unionId,Integer sex,String ip,Integer expireDay)
    {

        if(StringUtils.isEmpty(cellPhone))
        {
            throw new IllegalArgumentException("手机号码为空");
        }

        if(StringUtils.isEmpty(platform))
        {
            throw new IllegalArgumentException("使用平台为空");
        }

        if(StringUtils.isEmpty(openId))
        {
            throw new IllegalArgumentException("openid为空");
        }

        if(StringUtils.isEmpty(ip))
        {
            throw new IllegalArgumentException("ip为空");
        }
        if(expireDay<=0)
        {
            expireDay = 7;//默认7天
        }

        Member member = memberManager.findByCellPhone(cellPhone);
        MemberAuth  auth = null;

        if(member !=null)
        {
            auth = memberAuthRepository.findByMemberIdAndPlatform(member.getId(), platform);
            //
            if(StringUtils.isEmpty(member.getUserName()) || member.getUserName().equals(member.getCellPhone()))
            {
                member.setUserName(nickName);
            }
            memberManager.save(member);

            MemberInfo memberInfo = memberInfoManager.findById(member.getId());
            if(StringUtils.isEmpty(memberInfo.getHeadPortrait()))
            {
                memberInfo.setHeadPortrait(headImgUrl);
            }
            memberInfo.setSex(sex <=0 ?0 :1);

            memberInfoManager.save(memberInfo);
        }
        else
        {
            //新用户注册
            member = memberManager.register(cellPhone,nickName,cellPhone.substring(5),headImgUrl,sex);
            //发送条短信。告诉默认密码
        }

        if(auth ==null)
        {
            auth = memberAuthRepository.findOneByOpenId(openId,platform);
            if(auth == null) {
                Constants.logger.info("six...");
                auth = new MemberAuth();
                auth.setMemberId(member.getId());
                auth.setCreateTime(new Date());
            }
        }
        auth.setOpenId(openId);
        auth.setPlatform(platform);
        auth.setUpdateTime(new Date());
        auth.setUnionId(unionId);

        memberAuthRepository.save(auth);

        Constants.logger.info("seven...");

        return loginManager.login(member,platform,openId,ip,expireDay);
    }

    @Transactional
    public MemberLogin create(Integer memberId,String openId,String platform,String nickName,String headImgUrl,
                              String unionId,Integer sex,String ip,Integer expireDay)
    {

        if(StringUtils.isEmpty(platform))
        {
            throw new IllegalArgumentException("使用平台为空");
        }

        if(StringUtils.isEmpty(openId))
        {
            throw new IllegalArgumentException("open为空");
        }

        if(expireDay<=0)
        {
            expireDay = 7;//默认7天
        }

        MemberAuth auth = null;
        MemberLogin login = null;
        if(!StringUtils.isEmpty(openId))
        {
            //去取memberid
            auth = memberAuthRepository.findOneByOpenId(openId,platform);
            memberId = auth.getMemberId();
        }
        if(memberId>0)
        {
            if(auth != null) {
                auth = memberAuthRepository.findByMemberIdAndPlatform(memberId, platform);
            }
            //如果用户存在，则更新相关信息（为空的情况下）
            Member member = memberManager.findById(memberId);
            if(member == null)
            {
                throw new IllegalArgumentException("会员不存在");
            }
            if(StringUtils.isEmpty(member.getUserName()) || member.getUserName().equals(member.getCellPhone()))
            {
                member.setUserName(nickName);
            }
            memberManager.save(member);

            MemberInfo memberInfo = memberInfoManager.findById(memberId);
            if(StringUtils.isEmpty(memberInfo.getHeadPortrait()))
            {
                memberInfo.setHeadPortrait(headImgUrl);
            }
            memberInfo.setSex(sex <=0 ?0 :1);

            memberInfoManager.save(memberInfo);

            login = loginManager.login(member,platform,openId,ip,expireDay);
        }
        if(auth == null)
        {
            auth = new MemberAuth();
            auth.setMemberId(memberId);
            auth.setCreateTime(new Date());
        }
        auth.setOpenId(openId);
        auth.setPlatform(platform);
        auth.setUpdateTime(new Date());
        auth.setUnionId(unionId);
        memberAuthRepository.save(auth);

        return login;

    }

    private MemberAuth getByOpenId(String openId,String platform)
    {
        try {
            String sql = "SELECT * FROM MemberAuth WHERE openId=? and platform=? limit 1";
            List<Object> param = new ArrayList<>();
            param.add(openId);
            param.add(platform);

            return jt.queryForObject(sql, new BeanPropertyRowMapper<MemberAuth>(MemberAuth.class), param.toArray());
        }
        catch (Exception exp)
        {}
        return null;
    }
}

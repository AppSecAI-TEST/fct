package com.fct.member.service.business;

import com.fct.core.exceptions.BaseException;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.UUIDUtil;
import com.fct.member.data.entity.*;
import com.fct.member.data.repository.MemberInfoRepository;
import com.fct.member.interfaces.MemberDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by jon on 2017/5/7.
 */
@Service
public class MemberInfoManager {

    @Autowired
    private MemberInfoRepository memberInfoRepository;

    @Autowired
    private MemberManager memberManager;

    @Autowired
    private MemberBankInfoManager memberBankInfoManager;

    @Autowired
    private MemberStoreManager memberStoreManager;

    public void save(MemberInfo info)
    {
        if(info.getMemberId()<=0)
        {
            throw new IllegalArgumentException("会员不存在");
        }
        if(info.getSex() ==null)
        {
            info.setSex(0);
        }
        memberInfoRepository.save(info);
    }

    public MemberInfo findById(Integer memberId)
    {
        if(memberId<=0)
        {
            throw new IllegalArgumentException("会员不存在");
        }
        return memberInfoRepository.findOne(memberId);
    }

    @Transactional
    public void authentication(Integer memberId,String name,String identityCardNo,String identityCardImg,
                                     String bankName,String bankAccount)
    {
        if(memberId<=0)
        {
            throw new IllegalArgumentException("会员不存在");
        }
        if(StringUtils.isEmpty(name))
        {
            throw new IllegalArgumentException("姓名为空");
        }
        if(StringUtils.isEmpty(identityCardNo))
        {
            throw new IllegalArgumentException("身份证号码");
        }
        if(StringUtils.isEmpty(identityCardImg))
        {
            throw new IllegalArgumentException("身份证照片");
        }
        if(StringUtils.isEmpty(bankName))
        {
            throw new IllegalArgumentException("银行名称为空");
        }
        if(StringUtils.isEmpty(bankAccount))
        {
            throw new IllegalArgumentException("银行卡号");
        }

        Member member = memberManager.findById(memberId);

        if(member.getAuthStatus() ==1)
        {
            throw new BaseException("已认证");
        }

        member.setAuthStatus(1);//待审核认证
        memberManager.save(member);

        MemberInfo info = memberInfoRepository.findOne(memberId);
        info.setRealName(name);
        info.setIdentityCardImg(identityCardImg);
        info.setIdentityCardNo(identityCardNo);
        memberInfoRepository.save(info);

        MemberBankInfo bank = memberBankInfoManager.findOne(memberId);
        if(bank == null)
        {
            bank = new MemberBankInfo();
        }
        bank.setMemberId(memberId);
        bank.setCellPhone(member.getCellPhone());
        bank.setName(name);
        bank.setStatus(0);
        bank.setBankName(bankName);
        bank.setBankAccount(bankAccount);
        memberBankInfoManager.save(bank);

    }

    public MemberDTO findByMemberId(Integer id)
    {
        if(id <=0)
        {
            throw new IllegalArgumentException("用户不存在。");
        }
        Member member = memberManager.findById(id);

        MemberInfo info = findById(member.getId());

        MemberStore store = memberStoreManager.findByMemberId(member.getId());

        MemberDTO login = new MemberDTO();
        login.setCellPhone(member.getCellPhone());
        login.setMemberId(member.getId());
        login.setHeadPortrait(info.getHeadPortrait());
        login.setAuthStatus(member.getAuthStatus());
        login.setUserName(member.getUserName());
        login.setShopId(store!=null ?store.getId() :0);
        login.setGradeId(member.getGradeId());

        return login;
    }
}

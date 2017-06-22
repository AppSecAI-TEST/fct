package com.fct.member.service.business;

import com.fct.core.exceptions.BaseException;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.MemberBankInfo;
import com.fct.member.data.entity.MemberInfo;
import com.fct.member.data.repository.MemberInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void save(MemberInfo info)
    {
        memberInfoRepository.save(info);
    }

    public MemberInfo findById(Integer memberId)
    {
        return memberInfoRepository.findOne(memberId);
    }

    @Transactional
    public void authentication(Integer memberId,String name,String identityCardNo,String identityCardImg,
                                     String bankName,String bankAccount)
    {
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
}

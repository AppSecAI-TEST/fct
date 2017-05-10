package com.fct.member.service.business;

import com.fct.common.exceptions.BaseException;
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

    // 将自身的实例对象设置为一个属性,并加上Static和final修饰符
    private static final MemberInfoManager instance = new MemberInfoManager();

    // 静态方法返回该类的实例
    public static MemberInfoManager getInstance() {
        return instance;
    }

    @Autowired
    private MemberInfoRepository memberInfoRepository;

    public void save(MemberInfo info)
    {
        memberInfoRepository.saveAndFlush(info);
    }

    public MemberInfo findById(Integer memberId)
    {
        return memberInfoRepository.findOne(memberId);
    }

    @Transactional
    public void authentication(Integer memberId,String name,String identityCardNo,String identityCardImg,
                                     String bankName,String bankAccount)
    {
        Member member = MemberManager.getInstance().findById(memberId);

        if(member.getAuthStatus() ==1)
        {
            throw new BaseException("已认证");
        }

        member.setAuthStatus(1);//待审核认证
        MemberManager.getInstance().save(member);

        MemberInfo info = memberInfoRepository.findOne(memberId);
        info.setRealName(name);
        info.setIdentityCardImg(identityCardImg);
        info.setIdentityCardNo(identityCardNo);
        memberInfoRepository.saveAndFlush(info);

        MemberBankInfo bank = MemberBankInfoManager.getInstance().findOne(memberId);
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
        MemberBankInfoManager.getInstance().save(bank);

    }
}

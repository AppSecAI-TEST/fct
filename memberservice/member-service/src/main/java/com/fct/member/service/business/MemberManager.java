package com.fct.member.service.business;

import com.fct.common.utils.StringHelper;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.MemberBankInfo;
import com.fct.member.data.entity.MemberInfo;
import com.fct.member.data.repository.MemberInfoRepository;
import com.fct.member.data.repository.MemberRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/5.
 */
@Service
public class MemberManager {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberInfoManager memberInfoManager;

    @Autowired
    private MemberBankInfoManager memberBankInfoManager;

//    @Transactional
    public Member register(String cellPhone, String userName, String password)
    {
        if(StringUtils.isEmpty(cellPhone))
        {
            throw new IllegalArgumentException("手机号码为空。");
        }
        /*if(StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("用户名为空。");
        }*/
        if(StringUtils.isEmpty(password))
        {
            throw new IllegalArgumentException("密码为空。");
        }
        if(password.length()>32) {
            throw new IllegalArgumentException("密码太长了已超过32位。");
        }
        //校验手机是否存在，
        if(memberRepository.countByCellPhone(cellPhone)>0)
        {
            throw new IllegalArgumentException("手机号码已注册。");
        }
        if(!StringUtils.isEmpty(userName) && memberRepository.countByUserName(userName)>0)
        {
            throw new IllegalArgumentException("用户名已存在。");
        }

        Member member = new Member();
        member.setUserName(userName);
        member.setCellPhone(cellPhone);
        member.setPassword(StringHelper.md5(password));
        member.setRegisterTime(new Date());
        memberRepository.save(member);

        //同步注册memberInfo
        MemberInfo info = new MemberInfo();
        info.setMemberId(member.getId());
        memberInfoManager.save(info);
        return member;
    }

    public Member findById(Integer id)
    {
        return memberRepository.findOne(id);
    }

    public void updatePassword(Integer memberId,String oldPassword,String newPassword,String reNewPassword)
    {

        Member member = memberRepository.findOne(memberId);
        if(member == null)
        {
            throw new IllegalArgumentException("会员不存在。");
        }
        if(member.getPassword() != StringHelper.md5(oldPassword))
        {
            throw new IllegalArgumentException("旧密码不正确。");
        }
        if(newPassword!=reNewPassword)
        {
            throw new IllegalArgumentException("新密码与重复密码不一致。");
        }

        member.setPassword(StringHelper.md5(newPassword));
        memberRepository.saveAndFlush(member);
    }

    public void forgetPassword(String cellPhone,String password)
    {
        memberRepository.updatePassword(cellPhone,password);
    }

    public void lock(Integer memberId)
    {
        memberRepository.lock(memberId);
    }

    public Member login(String cellPhone,String password)
    {
        return  memberRepository.login(cellPhone, StringHelper.md5(password));
    }

    public Member save(Member member)
    {
        return memberRepository.saveAndFlush(member);
    }

    /// <summary>
    /// 增加邀请数
    /// </summary>
    public void addInviteCount(Integer memberId, Integer count)
    {
        memberRepository.addInviteCount(memberId,count);
    }



    public void verifyAuthStatus(Integer memberId)
    {
        memberRepository.verifyAuthStatus(memberId);

        MemberBankInfo bank = memberBankInfoManager.findOne(memberId);
        bank.setStatus(1-bank.getStatus());  //审核通过
        memberBankInfoManager.save(bank);

    }

    public Page<Member> findAll(String cellPhone, String beginTime,String endTime,Integer pageIndex, Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<Member> spec = new Specification<Member>() {
            @Override
            public Predicate toPredicate(Root<Member> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(cellPhone)) {
                    predicates.add(cb.equal(root.get("cellPhone"), cellPhone));
                }
                if (!StringUtils.isEmpty(beginTime)) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("registerTime"), beginTime));
                }
                if (!StringUtils.isEmpty(endTime)) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("registerTime"), endTime));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return memberRepository.findAll(spec,pageable);
    }

}

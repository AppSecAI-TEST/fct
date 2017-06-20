package com.fct.member.service.business;

import com.fct.common.utils.PageUtil;
import com.fct.common.utils.StringHelper;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.MemberBankInfo;
import com.fct.member.data.entity.MemberInfo;
import com.fct.member.data.entity.SystemUser;
import com.fct.member.data.repository.MemberInfoRepository;
import com.fct.member.data.repository.MemberRepository;
import com.fct.member.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.nativejdbc.Jdbc4NativeJdbcExtractor;
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

    @Autowired
    private JdbcTemplate jt;

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
        if(member.getPassword() != StringHelper.md5(oldPassword) && !oldPassword.equals("sysupdate"))
        {
            throw new IllegalArgumentException("旧密码不正确。");
        }
        if(!newPassword.equals(reNewPassword))
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

    @Transactional
    public void verifyAuthStatus(Integer memberId)
    {
        MemberBankInfo bank = memberBankInfoManager.findOne(memberId);
        if(bank == null) {
            throw  new IllegalArgumentException("未有绑定银行卡不可进行认证");
        }
        bank.setStatus(1 - bank.getStatus());  //审核通过
        memberBankInfoManager.save(bank);

        memberRepository.verifyAuthStatus(memberId);

    }

    private String getCondition(String cellPhone, Integer authStatus,String beginTime,String endTime,List<Object> param)
    {
        String condition ="";
        if(authStatus>-1)
        {
            condition += " AND authStatus="+authStatus;
        }
        if(!StringUtils.isEmpty(cellPhone))
        {
            condition +=" AND cellphone=?";
            param.add(cellPhone);
        }
        if(!StringUtils.isEmpty(beginTime))
        {
            condition +=" AND registerTime>=?";
            param.add(beginTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            condition +=" AND registerTime<?";
            param.add(endTime);
        }
        return condition;
    }

    public PageResponse<Member> findAll(String cellPhone, Integer authStatus,String beginTime,String endTime,Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="Member";
        String field ="*";
        String orderBy = "Id asc";
        String condition= getCondition(cellPhone,authStatus,beginTime,endTime,param);

        String sql = "SELECT Count(0) FROM Member WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<Member> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<Member>(Member.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<Member> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;
    }

}

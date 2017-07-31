package com.fct.member.service.business;

import com.fct.core.utils.PageUtil;
import com.fct.core.utils.StringHelper;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.MemberBankInfo;
import com.fct.member.data.entity.MemberInfo;
import com.fct.member.data.repository.MemberRepository;
import com.fct.member.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private MemberLoginManager memberLoginManager;

    @Autowired
    private JdbcTemplate jt;

    @Transactional
    public Member register(String cellPhone, String userName, String password,
                           String headImgUrl,Integer sex)
    {
        if(StringUtils.isEmpty(cellPhone))
        {
            throw new IllegalArgumentException("手机号码为空。");
        }
        if(StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("用户名为空。");
        }
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
        member.setAuthStatus(0);
        member.setCanInviteCount(0);
        member.setFailLoginCount(0);
        member.setGradeId(0);
        member.setLocked(0);
        member.setLoginCount(0);
        memberRepository.save(member);

        //同步注册memberInfo
        MemberInfo info = new MemberInfo();
        info.setMemberId(member.getId());
        info.setHeadPortrait(headImgUrl);
        info.setSex(sex <=0 ?0 :1);
        memberInfoManager.save(info);
        return member;
    }

    public int countByUserName(String userName)
    {
        return memberRepository.countByUserName(userName);
    }

    public Member findById(Integer id)
    {
        if(id<=0)
        {
            throw new IllegalArgumentException("用户id为空");
        }
        return memberRepository.findOne(id);
    }

    public Member findByCellPhone(String cellphone)
    {
        if(StringUtils.isEmpty(cellphone))
        {
            throw new IllegalArgumentException("手机号码为空");
        }
        return memberRepository.findByCellPhone(cellphone);
    }

    public void updatePassword(Integer memberId,String oldPassword,String newPassword,String reNewPassword)
    {
        if(memberId<=0)
        {
            throw new IllegalArgumentException("用户id为空");
        }
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
        memberRepository.save(member);
    }

    public void forgetPassword(String cellPhone,String password)
    {
        if(StringUtils.isEmpty(cellPhone))
        {
            throw new IllegalArgumentException("手机号码为空");
        }
        if(StringUtils.isEmpty(password))
        {
            throw new IllegalArgumentException("密码为空。");
        }
        memberRepository.updatePassword(cellPhone,password);
    }

    public void lock(Integer memberId)
    {
        if(memberId<=0)
        {
            throw new IllegalArgumentException("用户id为空");
        }
        memberRepository.lock(memberId);
    }

    public Member login(String cellPhone,String password)
    {
        if(StringUtils.isEmpty(cellPhone))
        {
            throw new IllegalArgumentException("手机号码为空");
        }
        if(StringUtils.isEmpty(password))
        {
            throw new IllegalArgumentException("密码为空。");
        }
        return  memberRepository.login(cellPhone, StringHelper.md5(password));
    }

    /***
     *
     * 此方法供内部使用，更新会员，不新增
     * */
    public Member save(Member member)
    {
        if(member.getId()<=0)
        {
            throw new IllegalArgumentException("用户id为空");
        }
        return memberRepository.save(member);
    }

    /// <summary>
    /// 增加邀请数
    /// </summary>
    public void addInviteCount(Integer memberId, Integer count)
    {
        if(memberId<=0)
        {
            throw new IllegalArgumentException("用户id为空");
        }
        if(count<=0)
        {
            throw new IllegalArgumentException("数量为空");
        }
        memberRepository.addInviteCount(memberId,count);
    }

    @Transactional
    public void verifyAuthStatus(Integer memberId,Integer authStatus)
    {
        if(memberId<=0)
        {
            throw new IllegalArgumentException("用户id为空");
        }
        MemberBankInfo bank = memberBankInfoManager.findOne(memberId);
        if(bank == null) {
            throw  new IllegalArgumentException("未有绑定银行卡不可进行认证");
        }
        bank.setStatus(1 - bank.getStatus());  //审核通过
        memberBankInfoManager.save(bank);

        Member member = memberRepository.findOne(memberId);
        if(member.getAuthStatus() ==0 && authStatus==2)
        {
            throw new IllegalArgumentException("非法操作");
        }
        member.setAuthStatus(authStatus);
        memberRepository.save(member);

        //更新memberlogin
        memberLoginManager.updateAudiStatus(memberId,member.getAuthStatus());
    }

    private String getCondition(String cellPhone, Integer authStatus,String beginTime,String endTime,List<Object> param)
    {
        StringBuilder sb = new StringBuilder();
        if(authStatus>-1)
        {
            sb.append(" AND authStatus="+authStatus);
        }
        if(!StringUtils.isEmpty(cellPhone))
        {
            sb.append("  AND cellphone=?");
            param.add(cellPhone);
        }
        if(!StringUtils.isEmpty(beginTime))
        {
            sb.append(" AND registerTime>=?");
            param.add(beginTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            sb.append("  AND registerTime<?");
            param.add(endTime);
        }
        return sb.toString();
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

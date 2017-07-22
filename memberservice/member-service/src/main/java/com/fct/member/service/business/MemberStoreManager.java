package com.fct.member.service.business;

import com.fct.core.utils.DateUtils;
import com.fct.core.utils.PageUtil;
import com.fct.member.data.entity.InviteCode;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.MemberStore;
import com.fct.member.data.repository.MemberStoreRepository;
import com.fct.member.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/7.
 */
@Service
public class MemberStoreManager {

    @Autowired
    private MemberStoreRepository memberStoreRepository;

    @Autowired
    private InviteCodeManager inviteCodeManager;

    @Autowired
    private MemberManager memberManager;

    @Autowired
    private JdbcTemplate jt;

    public MemberStore findByMemberId(Integer memberId) {
        return memberStoreRepository.findByMemberId(memberId);
    }

    @Transient
    public MemberStore apply(Integer memberId, String inviteCode) {

        if(memberId<=0)
        {
            throw new IllegalArgumentException("用户id为空");
        }
        if(StringUtils.isEmpty(inviteCode))
        {
            throw new IllegalArgumentException("邀请码为空");
        }
        InviteCode code = inviteCodeManager.findByCode(inviteCode);

        if (DateUtils.compareDate(new Date(), code.getExpireTime()) > 0 || code.getStatus() != 0) {
            throw new IllegalArgumentException("邀请码无效。");
        }
        Member member = memberManager.findById(memberId);

        if (memberStoreRepository.findByMemberId(memberId) != null) {
            throw new IllegalArgumentException("已有店铺存在。");
        }

        /*
        member.setInviterMemberId(code.getOwnerId());
        member.setInviterCellPhone(code.getOwnerCellPhone());
        memberManager.save(member);*/

        code.setStatus(1);//已使用
        code.setToMemberId(memberId);
        code.setToCellPhone(member.getCellPhone());
        code.setUseTime(new Date());
        inviteCodeManager.save(code);

        MemberStore ms = new MemberStore();
        ms.setMemberId(memberId);
        ms.setCreateTime(new Date());
        ms.setCellPhone(member.getCellPhone());
        ms.setStatus(0);
        ms.setInviterMemberId(code.getOwnerId());
        ms.setInviterCellPhone(code.getOwnerCellPhone());
        memberStoreRepository.save(ms);

        return ms;
    }

    public MemberStore findById(Integer id)
    {
        return memberStoreRepository.findOne(id);
    }

    public void updateStatus(Integer id)
    {
        if(id<=0)
        {
            throw new IllegalArgumentException("店铺id为空");
        }
        memberStoreRepository.updateStatus(id);
    }

    private String getCondition(String cellPhone, Integer status, List<Object> param)
    {
        StringBuilder sb = new StringBuilder();
        if(!StringUtils.isEmpty(cellPhone))
        {
            sb.append("  AND (cellPhone=? OR id=?)");
            param.add(cellPhone);
            param.add(cellPhone);
        }
        if(status>-1)
        {
            sb.append("  AND status="+status);
        }
        return sb.toString();
    }

    public PageResponse<MemberStore> findAll(String cellPhone, Integer status, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="MemberStore";
        String field ="*";
        String orderBy = "Id asc";
        String condition= getCondition(cellPhone,status,param);

        String sql = "SELECT Count(0) FROM MemberStore WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<MemberStore> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<MemberStore>(MemberStore.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<MemberStore> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;
    }
}

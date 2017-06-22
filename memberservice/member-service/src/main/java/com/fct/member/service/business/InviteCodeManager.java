package com.fct.member.service.business;

import com.fct.core.utils.DateUtils;
import com.fct.core.utils.PageUtil;
import com.fct.core.utils.StringHelper;
import com.fct.member.data.entity.InviteCode;
import com.fct.member.data.entity.Member;
import com.fct.member.data.repository.InviteCodeRepository;
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
 * Created by jon on 2017/5/8.
 */
@Service
public class InviteCodeManager {

    @Autowired
    private InviteCodeRepository inviteCodeRepository;

    @Autowired
    private MemberManager memberManager;

    @Autowired
    private JdbcTemplate jt;

    @Transactional
    public void create(Integer memberId)
    {
        Member member = memberManager.findById(memberId);
        if (member == null || member.getCanInviteCount() == 0)
        {
            throw new IllegalArgumentException("您没有生成邀请码权利！");
        }

        InviteCode code = new InviteCode();
        code.setOwnerId(member.getId());
        code.setOwnerCellPhone(member.getCellPhone());
        code.setCreateTime(new Date());
        code.setExpireTime(DateUtils.addDay(new Date(),7));   //邀请码7天有效期
        code.setCode(StringHelper.getRandomString(8));  //随机8位

        inviteCodeRepository.save(code);

        member.setCanInviteCount(member.getCanInviteCount()-1);
        memberManager.save(member);
    }

    public String getCondition(String code,Integer ownerId, String ownerCellPhone, String toCellphone,List<Object> param)
    {
        String condition ="";
        if(ownerId>0)
        {
            condition +=" AND ownerId="+ownerId;
        }
        if(!StringUtils.isEmpty(ownerCellPhone))
        {
            condition += " AND ownerCellPhone=?";
            param.add(ownerCellPhone);
        }
        if(!StringUtils.isEmpty(code))
        {
            condition += " AND code=?";
            param.add(code);
        }
        if(!StringUtils.isEmpty(toCellphone))
        {
            condition +=" AND toCellPhone=?";
            param.add(toCellphone);
        }
        return condition;
    }

    public PageResponse<InviteCode> findAll(String code,Integer ownerId, String ownerCellPhone, String toCellphone,
                                            int pageIndex,int pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="InviteCode";
        String field ="*";
        String orderBy = "Id asc";
        String condition= getCondition(code,ownerId,ownerCellPhone,toCellphone,param);

        String sql = "SELECT Count(0) FROM InviteCode WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<InviteCode> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<InviteCode>(InviteCode.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<InviteCode> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);
        
        return p;
    }

    public InviteCode findByCode(String code)
    {
        return  inviteCodeRepository.findByCode(code);
    }

    public void save(InviteCode code)
    {
        inviteCodeRepository.saveAndFlush(code);
    }
}

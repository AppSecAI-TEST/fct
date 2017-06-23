package com.fct.member.service.business;

import com.fct.core.utils.PageUtil;
import com.fct.member.data.entity.MemberBankInfo;
import com.fct.member.data.repository.MemberBankInfoRepository;
import com.fct.member.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/8.
 */
@Service
public class MemberBankInfoManager {

    @Autowired
    private MemberBankInfoRepository memberBankInfoRepository;

    @Autowired
    private JdbcTemplate jt;

    public void save(MemberBankInfo info)
    {
        if(info.getMemberId()<=0)
        {
            throw new IllegalArgumentException("会员不存在");
        }
        if(StringUtils.isEmpty(info.getCellPhone()))
        {
            throw new IllegalArgumentException("电话为空。");
        }
        if(StringUtils.isEmpty(info.getName()))
        {
            throw new IllegalArgumentException("姓名为空");
        }
        if(StringUtils.isEmpty(info.getBankName()))
        {
            throw new IllegalArgumentException("银行名称为空");
        }
        if(StringUtils.isEmpty(info.getBankAccount()))
        {
            throw new IllegalArgumentException("银行账号为空");
        }
        if(info.getId() ==null || info.getId()<=0)
        {
            info.setStatus(0);
            info.setCreateTime(new Date());
        }
        memberBankInfoRepository.save(info);
    }

    public MemberBankInfo findById(Integer id)
    {
        if(id<=0)
        {
            throw new IllegalArgumentException("id为空。");
        }
        return memberBankInfoRepository.findOne(id);
    }

    public MemberBankInfo findOne(Integer memberId)
    {
        if(memberId<=0)
        {
            throw new IllegalArgumentException("会员为空。");
        }
        return memberBankInfoRepository.findOne(memberId);
    }

    public String getCondition(String cellPhone,String bankName,Integer status,List<Object> param)
    {
        String condition="";
        if(!StringUtils.isEmpty(cellPhone))
        {
            condition += " AND cellphone=?";
            param.add(cellPhone);
        }
        if(!StringUtils.isEmpty(bankName))
        {
            condition += " AND bankName like ?";
            param.add("%"+ bankName +"%");
        }
        if(status>-1)
        {
            condition += " AND status="+status;
        }
        return condition;
    }

    public PageResponse<MemberBankInfo> findAll(String cellPhone,String bankName,Integer status,Integer pageIndex,
                                        Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="MemberBankInfo";
        String field ="*";
        String orderBy = "Id asc";
        String condition= getCondition(cellPhone,bankName,status,param);

        String sql = "SELECT Count(0) FROM MemberBankInfo WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<MemberBankInfo> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<MemberBankInfo>(MemberBankInfo.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<MemberBankInfo> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;
    }
}

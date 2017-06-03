package com.fct.finance.service.business;

import com.fct.common.utils.PageUtil;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.repository.MemberAccountRepository;
import com.fct.finance.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
@Service
public class MemberAccountManager {

    @Autowired
    private MemberAccountRepository memberAccountRepository;

    @Autowired
    private JdbcTemplate jt;

    public void save(MemberAccount account)
    {
        memberAccountRepository.saveAndFlush(account);
    }

    public MemberAccount findById(int memberId)
    {
        return memberAccountRepository.findOne(memberId);
    }

    private String getCondition(String cellPhone,List<Object> param)
    {
        String condition ="";
        if (!StringUtils.isEmpty(cellPhone)) {
            condition+=" AND cellPhone=?";
            param.add(cellPhone);
        }
        return condition;
    }

    public PageResponse<MemberAccount> findAll(String cellPhone, Integer orderBy, Integer pageIndex, Integer pageSize)
    {
        String sortName = " CreateTime";
        switch (orderBy)
        {
            case 1:
                sortName = "availableAmount Desc";
                break;
            case 2:
                sortName = "points Desc";
                break;
            case 3:
                sortName = "accumulateIncome Desc";
                break;
        }

        List<Object> param = new ArrayList<>();

        String table="MemberAccount";
        String field ="*";
        String condition= getCondition(cellPhone,param);

        String sql = "SELECT Count(0) FROM MemberAccount WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,sortName,pageIndex,pageSize);

        List<MemberAccount> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<MemberAccount>(MemberAccount.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<MemberAccount> pageResponse = new PageResponse<>();
        pageResponse.setTotalCount(count);
        pageResponse.setCurrent(end);
        pageResponse.setElements(ls);
        pageResponse.setHasMore(hasmore);

        return pageResponse;
    }

}

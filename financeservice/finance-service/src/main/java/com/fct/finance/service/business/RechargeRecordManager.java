package com.fct.finance.service.business;

import com.fct.common.converter.DateFormatter;
import com.fct.common.utils.PageUtil;
import com.fct.finance.data.entity.RechargeRecord;
import com.fct.finance.data.entity.SettleRecord;
import com.fct.finance.data.repository.RechargeRecordRepository;
import com.fct.finance.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/4/21.
 */
@Service
public class RechargeRecordManager {

    @Autowired
    private RechargeRecordRepository rechargeRecordRepository;

    @Autowired
    private JdbcTemplate jt;

    public Integer create(RechargeRecord record)
    {
        if(record.getMemberId()<=0)
        {
            throw new IllegalArgumentException("用户Id为空。");
        }
        if(StringUtils.isEmpty(record.getCellPhone()))
        {
            throw new IllegalArgumentException("手机号为空。");
        }
        record.setCreateTime(new Date());

        rechargeRecordRepository.save(record);

        return record.getId();
    }

    public RechargeRecord findById(Integer id)
    {
        return rechargeRecordRepository.findOne(id);
    }

    public void paySuccess(Integer id, String payOrderId, String payPlatform, String payTime,String payStatus)
    {
        RechargeRecord record = rechargeRecordRepository.findOne(id);
        record.setPayOrderId(payOrderId);
        record.setPayPlatform(payPlatform);
        record.setPayTime(DateFormatter.parseDateTime(payTime));
        if(payStatus =="success") {
            record.setStatus(1);    //充值成功
        }
        else
        {
            record.setStatus(2);    //充值失败
        }
        rechargeRecordRepository.save(record);
    }

    private String getCondition(Integer memberId, String cellPhone, Integer status,
                                String beginTime, String endTime,List<Object> param)
    {
        String condition = "";
        if (!StringUtils.isEmpty(cellPhone)) {
            condition +=" AND cellPhone=?";
            param.add(cellPhone);
        }

        if (memberId > 0) {
            condition +=" AND memberId="+memberId;
        }

        if (!StringUtils.isEmpty(beginTime)) {
            condition +=" AND createTime>=?";
            param.add(beginTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            condition +=" AND endTime<?";
            param.add(endTime);
        }
        if (status > -1) {
            condition +=" AND status="+status;
        }
        return condition;
    }

    public PageResponse<RechargeRecord> findAll(Integer memberId, String cellPhone, Integer status,
                                        String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="RechargeRecord";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getCondition(memberId,cellPhone,status,
                beginTime,endTime,param);

        String sql = "SELECT Count(0) FROM RechargeRecord WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<RechargeRecord> ls = jt.query(sql, param.toArray(),
                new BeanPropertyRowMapper<RechargeRecord>(RechargeRecord.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<RechargeRecord> pageResponse = new PageResponse<>();
        pageResponse.setTotalCount(count);
        pageResponse.setCurrent(end);
        pageResponse.setElements(ls);
        pageResponse.setHasMore(hasmore);

        return pageResponse;
    }
}

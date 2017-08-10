package com.fct.finance.service.business;

import com.fct.core.utils.DateUtils;
import com.fct.core.utils.PageUtil;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.MemberAccountHistory;
import com.fct.finance.data.entity.SettleRecord;
import com.fct.finance.data.repository.SettleRecordRepository;
import com.fct.finance.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
@Service
public class SettleRecordManager {

    @Autowired
    private SettleRecordRepository settleRecordRepository;

    @Autowired
    private MemberAccountManager memberAccountManager;

    @Autowired
    private MemberAccountHistoryManager memberAccountHistoryManager;

    @Autowired
    private JdbcTemplate jt;

    public Integer create(SettleRecord record)
    {
        if(record.getMemberId() <=0)
        {
            throw new IllegalArgumentException("结算店长用户id为空。");
        }
        if(record.getShopId()<=0)
        {
            throw new IllegalArgumentException("店铺id为空。");
        }
        if(StringUtils.isEmpty(record.getTradeId()))
        {
            throw new IllegalArgumentException("交易订单号为空。");
        }
        if(StringUtils.isEmpty(record.getTradeType()))
        {
            throw new IllegalArgumentException("交易订单类型为空。");
        }
        if(record.getSaleAmount().doubleValue()<=0 || record.getCommission().doubleValue()<=0)
        {
            throw new IllegalArgumentException("金额非法。");
        }

        if (record.getCommission().doubleValue() >= record.getSaleAmount().doubleValue())
        {
            throw new IllegalArgumentException("结算金额不合法");
        }

        if(settleRecordRepository.countByTradeIdAndTradeType(record.getTradeId(),record.getTradeType())>0)
        {
            throw new IllegalArgumentException("已结算");
        }
        if(record.getInviterId()>0)
        {
            //额外奖励推荐者
            record.setInviterCommission(record.getCommission().multiply(new BigDecimal("0.1")));
        }
        else
        {
            record.setInviterCommission(new BigDecimal(0));
        }

        record.setStatus(0);
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        settleRecordRepository.save(record);
        return record.getId();
    }

    public void updateStatus(Integer omsOperaterId,Integer status,String ids,String desc)
    {
        if(status ==2)
        {
            throw  new IllegalArgumentException("非法操作！");
        }
        if(StringUtils.isEmpty(ids))
        {
            throw new IllegalArgumentException("id为空");
        }

        String sql = "UPDATE SettleRecord SET Status=?,remark=?,omsOperaterId=?,updateTime=? WHERE Id IN ("+ ids +") AND Status!=1";
        List<Object> param = new ArrayList<>();
        param.add(status);
        param.add(desc);
        param.add(omsOperaterId);
        param.add(DateUtils.format(new Date()));

        jt.update(sql,param.toArray());
    }

    /// <summary>
    /// 将财务确认结算数据同步更新到用户虚拟账户
    /// </summary>
    public void task()
    {
        List<SettleRecord> ls = settleRecordRepository.findByStatus(1);
        for (SettleRecord sr:ls
             ) {
            settleAmount(sr);
        }

    }

    @Transactional
    private void settleAmount(SettleRecord sr)
    {
        sr.setStatus(2);    //结算成功
        sr.setUpdateTime(new Date());
        settleRecordRepository.save(sr);

        memberAccountManager.addAccountAmount(sr.getMemberId(),sr.getCellPhone(),sr.getCommission(),
                new BigDecimal(0),sr.getCommission(),0,Constants.enumTradeType.settle.toString(),
                sr.getId().toString(),1,"佣金结算");

        if(sr.getInviterId()>0 && sr.getInviterCommission().doubleValue()>0)
        {
            memberAccountManager.addAccountAmount(sr.getInviterId(),sr.getInviterCellPhone(),sr.getInviterCommission(),
                    new BigDecimal(0),sr.getInviterCommission(),0,Constants.enumTradeType.reward.toString(),
                    sr.getId().toString(),1,"奖励佣金");
        }
    }

    public SettleRecord findById(Integer id) {
        if (id <= 0)
        {
            throw new IllegalArgumentException("id为空");
        }
        return settleRecordRepository.findOne(id);
    }

    private String getCondition(Integer memberId, String cellPhone, String tradeType, String tradeId, Integer status,
                                String beginTime, String endTime,List<Object> param)
    {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(cellPhone)) {
            sb.append(" AND cellPhone=?");
            param.add(cellPhone);
        }

        if (!StringUtils.isEmpty(tradeType)) {
            sb.append(" AND tradeType=?");
            param.add(tradeType);
        }

        if (!StringUtils.isEmpty(tradeId)) {
            sb.append(" AND tradeId=?");
            param.add(tradeId);
        }

        if(memberId>0)
        {
            sb.append(" AND memberId="+memberId);
        }
        if(status>-1)
        {
            sb.append(" AND status="+status);
        }
        if (!StringUtils.isEmpty(beginTime)) {
            sb.append(" AND createTime >=?");
            param.add(beginTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            sb.append(" AND createTime <?");
            param.add(endTime);
        }
        return sb.toString();
    }

    public PageResponse<SettleRecord> findAll(Integer memberId, String cellPhone, String tradeType, String tradeId, Integer status,
                                      String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="SettleRecord";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getCondition(memberId,cellPhone,tradeType,tradeId,status,
                beginTime,endTime,param);

        String sql = "SELECT Count(0) FROM SettleRecord WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<SettleRecord> ls = jt.query(sql, param.toArray(),
                new BeanPropertyRowMapper<SettleRecord>(SettleRecord.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<SettleRecord> pageResponse = new PageResponse<>();
        pageResponse.setTotalCount(count);
        pageResponse.setCurrent(end);
        pageResponse.setElements(ls);
        pageResponse.setHasMore(hasmore);

        return pageResponse;
    }
}

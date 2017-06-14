package com.fct.finance.service.business;

import com.fct.common.utils.PageUtil;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.MemberAccountHistory;
import com.fct.finance.data.entity.SettleRecord;
import com.fct.finance.data.entity.WithdrawRecord;
import com.fct.finance.data.repository.SettleRecordRepository;
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

        record.setStatus(0);
        settleRecordRepository.save(record);
        return record.getId();
    }

    public void updateStatus(Integer omsOperaterId,Integer status,String ids,String desc)
    {
        String sql = "UPDATE SettleRecord SET Status=?,remark=?,omsOperaterId=?,updateTime=? WHERE Id IN(?) AND Status!=1";
        List<Object> param = new ArrayList<>();
        param.add(status);
        param.add(desc);
        param.add(omsOperaterId);
        param.add(new Date());
        param.add(ids);

        jt.update(sql,param);
    }

    /// <summary>
    /// 将财务确认结算数据同步更新到用户虚拟账户
    /// </summary>
    public void task()
    {

        List<SettleRecord> ls = settleRecordRepository.findByStatus(1);
        for (SettleRecord sr:ls
             ) {
            sr.setStatus(2);    //结算成功
            sr.setUpdateTime(new Date());
            settleRecordRepository.save(sr);

            addAccountAmount(sr.getMemberId(),sr.getCellPhone(),sr.getCommission(),sr.getId());

            if(sr.getInviterId()>0 && sr.getInviterCommission().doubleValue()>0)
            {
                addAccountAmount(sr.getInviterId(),"",sr.getInviterCommission(),sr.getId());
            }

        }

    }

    /// <summary>
    /// 增加虚拟余额
    /// </summary>
    void addAccountAmount(Integer memberId,String cellPhone,BigDecimal commission,Integer settleId)
    {
        MemberAccount account = memberAccountManager.findById(memberId);

        if (account == null)
        {
            account = new MemberAccount();
            account.setMemberId(memberId);
            account.setCellPhone(cellPhone);
            account.setCreateTime(new Date());
        }
        account.setAvailableAmount(account.getAvailableAmount().add(commission));
        account.setAccumulateIncome(account.getAccumulateIncome().add(commission));
        account.setWithdrawAmount(account.getWithdrawAmount().add(commission));

        memberAccountManager.save(account);

        MemberAccountHistory history = new MemberAccountHistory();
        history.setTradeId(settleId.toString());
        history.setTradeType(Constants.enumTradeType.settle.toString());
        history.setMemberId(memberId);
        history.setAmount(commission);
        history.setBalanceAmount(account.getAvailableAmount());
        history.setPoints(0);
        history.setBalancePoints(account.getPoints());
        history.setRemark("佣金结算");
        history.setBehaviorType(1); //收入
        memberAccountHistoryManager.Create(history);

    }

    public SettleRecord findById(Integer id)
    {
        return settleRecordRepository.findOne(id);
    }

    private String getCondition(Integer memberId, String cellPhone, String tradeType, String tradeId, Integer status,
                                String beginTime, String endTime,List<Object> param)
    {
        String condition = "";
        if (!StringUtils.isEmpty(cellPhone)) {
            condition +=" AND cellPhone=?";
            param.add(cellPhone);
        }

        if (!StringUtils.isEmpty(tradeType)) {
            condition +=" AND tradeType=?";
            param.add(tradeType);
        }

        if (!StringUtils.isEmpty(tradeId)) {
            condition +=" AND tradeId=?";
            param.add(tradeId);
        }

        if(memberId>0)
        {
            condition+=" AND memberId="+memberId;
        }
        if(status>-1)
        {
            condition+=" AND status="+status;
        }
        if (!StringUtils.isEmpty(beginTime)) {
            condition +=" AND createTime >=?";
            param.add(beginTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            condition +=" AND createTime <?";
            param.add(endTime);
        }
        return condition;
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

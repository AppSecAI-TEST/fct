package com.fct.finance.service.business;

import com.fct.core.utils.PageUtil;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.MemberAccountHistory;
import com.fct.finance.data.entity.WithdrawRecord;
import com.fct.finance.data.repository.WithdrawRecordRepository;
import com.fct.finance.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
@Service
public class WithdrawRecordManager {
    @Autowired
    private WithdrawRecordRepository withdrawRecordRepository;

    @Autowired
    private MemberAccountManager memberAccountManager;

    @Autowired
    private MemberAccountHistoryManager memberAccountHistoryManager;

    @Autowired
    private JdbcTemplate jt;

    @Transactional
    public void apply(WithdrawRecord record)
    {

        if (record.getMemberId() <= 0)
        {
            throw new IllegalArgumentException("会员不存在");
        }
        if (StringUtils.isEmpty(record.getName()))
        {
            throw new IllegalArgumentException("提现账户姓名为空");
        }
        if (StringUtils.isEmpty(record.getBankName()))
        {
            throw new IllegalArgumentException("提现平台为空");
        }
        if (StringUtils.isEmpty(record.getBankAccount()))
        {
            throw new IllegalArgumentException("提现账号为空");
        }

        if (record.getAmount().doubleValue() <= 0)
        {
            throw new IllegalArgumentException("提现金额为空。");
        }

        if(withdrawRecordRepository.countByMemberIdAndStatus(record.getMemberId(),0)>0)
        {
            throw new IllegalArgumentException("还有尚未处理的提现,不可连续申请。");
        }

        MemberAccount account = memberAccountManager.findById(record.getMemberId());
        if(account == null || account.getWithdrawAmount().doubleValue() <record.getAmount().doubleValue())
        {
            throw  new IllegalArgumentException("非法操作。");
        }

        record.setStatus(0);
        record.setCreateTime(new Date());
        record.setUpdateTime(record.getCreateTime());
        withdrawRecordRepository.save(record);

        account.setAvailableAmount(account.getAvailableAmount().subtract(record.getAmount()));
        account.setWithdrawAmount(account.getWithdrawAmount().subtract(record.getAmount()));

        memberAccountManager.save(account);    //

        memberAccountHistoryManager.add(account,record.getAmount(),new BigDecimal(0),record.getAmount(),0,
                Constants.enumTradeType.withdraw.toString(),record.getId().toString(),0,"提现");

    }

    @Transactional
    public void updateStatus(Integer omsOperaterId,Integer id, Integer status,String desc)
    {
        if(id<=0)
        {
            throw new IllegalArgumentException("id为空");
        }
        WithdrawRecord record = withdrawRecordRepository.findOne(id);
        if(record.getStatus()>0)
        {
            throw new IllegalArgumentException("非法操作");
        }
        record.setStatus(status);
        record.setRemark(desc);
        record.setOmsOperaterId(omsOperaterId);
        record.setUpdateTime(new Date());

        withdrawRecordRepository.save(record);

        //如果为拒绝退款，提现金额则返回
        if(status ==2)
        {
            memberAccountManager.addAccountAmount(record.getMemberId(),"",record.getAmount(),
                    new BigDecimal(0),record.getAmount(),0,Constants.enumTradeType.withdraw_refund.toString(),
                    record.getId().toString(),1,"提现失败退回金额");
        }
    }

    private String getCondition(Integer memberId, String cellPhone, Integer status,
                                String beginTime, String endTime,List<Object> param)
    {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(cellPhone)) {
            sb.append(" AND cellPhone=?");
            param.add(cellPhone);
        }
        if(memberId>0)
        {
            sb.append(" AND memberId="+memberId);
            param.add(memberId);
        }
        if(status>-1)
        {
            sb.append(" AND Status="+status);
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

    public PageResponse<WithdrawRecord> findAll(Integer memberId, String cellPhone, Integer status,
                                        String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="WithdrawRecord";
        String field ="*";
        String orderBy = "createTime Desc";
        String condition= getCondition(memberId,cellPhone,status,beginTime,endTime,param);

        String sql = "SELECT Count(0) FROM WithdrawRecord WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<WithdrawRecord> ls = jt.query(sql, param.toArray(),
                new BeanPropertyRowMapper<WithdrawRecord>(WithdrawRecord.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<WithdrawRecord> pageResponse = new PageResponse<>();
        pageResponse.setTotalCount(count);
        pageResponse.setCurrent(end);
        pageResponse.setElements(ls);
        pageResponse.setHasMore(hasmore);

        return pageResponse;
    }
}

package com.fct.finance.service.business;

import com.fct.core.converter.DateFormatter;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.PageUtil;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.MemberAccountHistory;
import com.fct.finance.data.entity.RechargeRecord;
import com.fct.finance.data.repository.RechargeRecordRepository;
import com.fct.finance.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
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
    private MemberAccountManager memberAccountManager;

    @Autowired
    private MemberAccountHistoryManager memberAccountHistoryManager;

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
        /*if(record.getAmount().doubleValue()<=0)
        {
            throw new IllegalArgumentException("充值金额不合法。");
        }*/
        if(record.getPayAmount().doubleValue()<=0)
        {
            throw new IllegalArgumentException("应付金额不合法。");
        }
        if(record.getGiftAmount().doubleValue()<=0) {
            record.setGiftAmount(new BigDecimal(0));
        }
        record.setStatus(0);
        record.setExpiredTime(DateUtils.addDay(new Date(),2)); //2天过期
        record.setAmount(record.getPayAmount().add(record.getGiftAmount()));
        record.setCreateTime(new Date());

        rechargeRecordRepository.save(record);

        return record.getId();
    }

    public RechargeRecord findById(Integer id)
    {
        if(id<=0)
        {
            throw new IllegalArgumentException("id为空");
        }
        return rechargeRecordRepository.findOne(id);
    }

    @Transactional
    public void paySuccess(Integer id, String payOrderId, String payPlatform, String payTime,Integer payStatus)
    {
        if(id<=0)
        {
            throw new IllegalArgumentException("id为空");
        }
        if(StringUtils.isEmpty(payOrderId))
        {
            throw new IllegalArgumentException("支付订单号为空");
        }
        if(StringUtils.isEmpty(payPlatform))
        {
            throw new IllegalArgumentException("支付平台为空");
        }
        if(StringUtils.isEmpty(payTime))
        {
            throw new IllegalArgumentException("支付时间为空.");
        }
        if(payStatus == null || payStatus<=0)
        {
            throw new IllegalArgumentException("支付状态为空");
        }
        RechargeRecord record = rechargeRecordRepository.findOne(id);
        record.setPayOrderId(payOrderId);
        record.setPayPlatform(payPlatform);
        record.setPayTime(DateUtils.parseString(payTime));
        if(payStatus == 200) {
            record.setStatus(1);    //充值成功
            addAccountAmount(record.getMemberId(),record.getCellPhone(),record.getAmount(),
                    record.getId());
        }
        else
        {
            record.setStatus(2);    //充值失败
        }
        rechargeRecordRepository.save(record);
    }

    private void addAccountAmount(Integer memberId, String cellPhone, BigDecimal rechargeAmount, Integer rechargeId)
    {
        MemberAccount account = memberAccountManager.findById(memberId);

        if (account == null)
        {
            account = new MemberAccount();
            account.setMemberId(memberId);
            account.setCellPhone(cellPhone);
            account.setCreateTime(new Date());
            account.setAccumulateIncome(new BigDecimal(0));
            account.setAccumulatePoints(0);
            account.setAvailableAmount(new BigDecimal(0));
            account.setFrozenAmount(new BigDecimal(0));
            account.setPoints(0);
        }
        account.setAvailableAmount(account.getAvailableAmount().add(rechargeAmount));
        account.setRechargeAmount(account.getRechargeAmount().add(rechargeAmount));

        memberAccountManager.save(account);

        MemberAccountHistory history = new MemberAccountHistory();
        history.setTradeId(rechargeId.toString());
        history.setTradeType(Constants.enumTradeType.recharge.toString());
        history.setMemberId(memberId);
        history.setCellPhone(cellPhone);
        history.setAmount(rechargeAmount);
        history.setBalanceAmount(account.getAvailableAmount());
        history.setPoints(0);
        history.setBalancePoints(account.getPoints());
        history.setRemark("充值");
        history.setBehaviorType(1); //收入
        memberAccountHistoryManager.Create(history);

    }

    private String getCondition(Integer memberId, String cellPhone, String payPlayform,String payOrderId,
                                Integer status,Integer timeType,String beginTime, String endTime,List<Object> param)
    {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(cellPhone)) {
            sb.append(" AND cellPhone=?");
            param.add(cellPhone);
        }
        if (!StringUtils.isEmpty(payPlayform)) {
            sb.append(" AND payPlayform=?");
            param.add(payPlayform);
        }
        if (!StringUtils.isEmpty(payOrderId)) {
            sb.append(" AND payOrderId=?");
            param.add(payOrderId);
        }

        if (memberId > 0) {
            sb.append(" AND memberId="+memberId);
        }
        String time = "createTime";
        if(timeType ==1){
            time = "payTime";
        }
        if (!StringUtils.isEmpty(beginTime)) {
            sb.append(" AND "+time+">=?");
            param.add(beginTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            sb.append(" AND "+time+"<?");
            param.add(endTime);
        }
        if (status > -1) {
            sb.append(" AND status="+status);
        }
        return sb.toString();
    }

    public PageResponse<RechargeRecord> findAll(Integer memberId, String cellPhone, String payPlayform,String payOrderId,
                                                Integer status,Integer timeType,String beginTime, String endTime,
                                                Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="RechargeRecord";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getCondition(memberId,cellPhone,payPlayform,payOrderId,status,timeType,
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

    public void handleExpired()
    {
        String nowTime = DateUtils.format(new Date());
        String sql = String.format("UPDATE RechargeRecord set status = 2 WHERE status=0 AND expiredTime<'%s'",
                nowTime,nowTime);

        jt.update(sql);
    }

    public void updatePayPlatform(Integer id,String payPlatform) {
        rechargeRecordRepository.updatePayPlatform(payPlatform,id);
    }
}

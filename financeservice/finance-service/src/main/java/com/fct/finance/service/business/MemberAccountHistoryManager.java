package com.fct.finance.service.business;

import com.fct.core.utils.PageUtil;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.MemberAccountHistory;
import com.fct.finance.data.repository.MemberAccountHistoryRepository;
import com.fct.finance.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/4/20.
 */
@Service
public class MemberAccountHistoryManager {

    @Autowired
    private MemberAccountHistoryRepository memberAccountHistoryRepository;

    @Autowired
    private JdbcTemplate jt;

    public void Create(MemberAccountHistory history)
    {
        history.setCreateTime(new Date());

        memberAccountHistoryRepository.save(history);
    }

    public MemberAccountHistory findByTrade(String tradeId,String tradeType)
    {
        return memberAccountHistoryRepository.findByTradeIdAndTradeType(tradeId,tradeType);
    }

    public void add(MemberAccount account,BigDecimal amount,BigDecimal rechargeAmount, BigDecimal withdrawAmount,
                    Integer points,String trdeType,String trdeId,Integer behaviorType,String remark)
    {
        MemberAccountHistory history = new MemberAccountHistory();
        history.setTradeId(trdeId);
        history.setTradeType(trdeType);
        history.setMemberId(account.getMemberId());
        history.setCellPhone(account.getCellPhone());
        history.setAmount(amount);
        history.setBalanceAmount(account.getAvailableAmount());
        history.setPoints(points);
        history.setBalancePoints(account.getPoints());
        history.setRemark(remark);
        history.setBehaviorType(behaviorType); //支出
        history.setWithdrawAmount(withdrawAmount);
        history.setRechargeAmount(rechargeAmount);

        history.setCreateTime(new Date());

        memberAccountHistoryRepository.save(history);
    }

    public int getCountByTrade(String tradeId,String tradeType)
    {
        return memberAccountHistoryRepository.countByTradeIdAndTradeType(tradeId,tradeType);
    }

    private String getCondition(Integer memberId, String cellPhone, String tradeId, String tradeType,
                                String startTime,String endTime,List<Object> param)
    {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(cellPhone)) {
            sb.append(" AND cellPhone=?");
            param.add(cellPhone);
        }
        if (memberId>0) {
            sb.append(" AND memberId="+memberId);
        }
        if (!StringUtils.isEmpty(tradeId)) {
            sb.append(" AND tradeId=?");
            param.add(tradeId);
        }
        if (!StringUtils.isEmpty(tradeType)) {
            sb.append(" AND tradeType=?");
            param.add(tradeType);
        }
        if (!StringUtils.isEmpty(startTime)) {
            sb.append(" AND createtime >=?");
            param.add(startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            sb.append(" AND createtime <?");
            param.add(endTime);
        }

        return sb.toString();
    }

    public PageResponse<MemberAccountHistory> findAll(Integer memberId, String cellPhone, String tradeId, String tradeType,
                                              String startTime,String endTime,Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="MemberAccountHistory";
        String field ="*";
        String orderBy = "createTime Desc";
        String condition= getCondition(memberId,cellPhone,tradeId,tradeType,startTime,endTime,param);

        String sql = "SELECT Count(0) FROM MemberAccountHistory WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<MemberAccountHistory> ls = jt.query(sql, param.toArray(),
                new BeanPropertyRowMapper<MemberAccountHistory>(MemberAccountHistory.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<MemberAccountHistory> pageResponse = new PageResponse<>();
        pageResponse.setTotalCount(count);
        pageResponse.setCurrent(end);
        pageResponse.setElements(ls);
        pageResponse.setHasMore(hasmore);

        return pageResponse;
    }
}

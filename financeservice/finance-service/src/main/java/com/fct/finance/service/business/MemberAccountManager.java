package com.fct.finance.service.business;

import com.fct.core.utils.PageUtil;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.MemberAccountHistory;
import com.fct.finance.data.repository.MemberAccountRepository;
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
 * Created by jon on 2017/4/20.
 */
@Service
public class MemberAccountManager {

    @Autowired
    private MemberAccountRepository memberAccountRepository;

    @Autowired
    private MemberAccountHistoryManager memberAccountHistoryManager;

    @Autowired
    private JdbcTemplate jt;

    public void save(MemberAccount account)
    {
        memberAccountRepository.save(account);
    }

    public MemberAccount findById(int memberId) {
        if (memberId <= 0) {
            throw new IllegalArgumentException("会员id为空");
        }
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

    @Transactional
    public void giftPoints(String tradeId,String tradeType,Integer memberId,Integer points)
    {
        //判断是否重复执行;
        if(memberAccountHistoryManager.getCountByTrade(tradeId,tradeType)>0)
        {
            return;
        }

        MemberAccount account = findById(memberId);
        account.setPoints(account.getPoints()+points);
        account.setAccumulatePoints(account.getAccumulatePoints()+points);

        memberAccountRepository.save(account);

        memberAccountHistoryManager.add(account,new BigDecimal(0),new BigDecimal(0),new BigDecimal(0),
                points,tradeType,tradeId,1,"用户消费使用现金赠送同比积分");

    }


    public void addAccountAmount(Integer memberId, String cellPhone, BigDecimal amount,BigDecimal rechargeAmount,
                                 BigDecimal withdrawAmount, Integer points,String tradeType,String tradeId,
                                 Integer behaviorType,String remark)
    {
        //判断是否重复执行;
        if(memberAccountHistoryManager.getCountByTrade(tradeId,tradeType)>0)
        {
            return;
        }

        MemberAccount account = findById(memberId);

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
            account.setRechargeAmount(new BigDecimal(0));
            account.setWithdrawAmount(new BigDecimal(0));
        }
        account.setAvailableAmount(account.getAvailableAmount().add(amount));
        account.setRechargeAmount(account.getRechargeAmount().add(rechargeAmount));
        account.setWithdrawAmount(account.getWithdrawAmount().add(withdrawAmount));
        account.setPoints(account.getPoints()+points);

        memberAccountRepository.save(account);

        memberAccountHistoryManager.add(account,amount,rechargeAmount,withdrawAmount,
                points,tradeType,tradeId,behaviorType,remark);


    }

}

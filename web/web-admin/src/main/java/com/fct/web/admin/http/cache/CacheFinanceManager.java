package com.fct.web.admin.http.cache;

import com.fct.finance.data.entity.PayPlatform;
import com.fct.finance.interfaces.FinanceService;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CacheFinanceManager {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private CacheOrderManager cacheOrderManager;

    public List<PayPlatform> getPayPlatform()
    {
        try {
            return financeService.findPayPlatform("");
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return new ArrayList<>();
    }

    public String getPayPlatformName(String code)
    {
        List<PayPlatform> list = getPayPlatform();
        for (PayPlatform pay: list
                ) {
            if(pay.getCode().equals(code))
                return pay.getName();
        }
        return "";
    }

    public Map<String,String> getTradeType()
    {
        Map<String,String> map = new HashMap<>();
        map.put("recharge","充值");
        map.put("buy","购买宝贝");
        map.put("withdraw","提现");
        map.put("settle","销售结算");
        map.put("refund","退款");
        map.put("withdraw_refund","提现退款");
        return map;
    }

    public String getTradeTypeName(String key)
    {
        return getTradeType().get(key);
    }

    public Map<Integer,String> getPayStatus()
    {
        Map<Integer,String> map = new HashMap<>();
        map.put(0,"待付款");
        map.put(1,"支付成功");
        map.put(2,"超时关闭");
        map.put(3,"支付异常");
        map.put(4,"全部退款");
        return map;
    }

    public String getPayStatusName(Integer key)
    {
        return getPayStatus().get(key);
    }

    public Map<Integer,String> getSettleStatus()
    {
        Map<Integer,String> map = new HashMap<>();
        map.put(0,"待确认");
        map.put(1,"财务已确认");
        map.put(2,"结算成功");
        map.put(3,"拒绝");
        return map;
    }

    public String getSettleStatusName(Integer key)
    {
        return getSettleStatus().get(key);
    }

    public String getRefundTypeName(Integer type)
    {
        return cacheOrderManager.getRefundTypeName(type);
    }

    public Map<Integer,String> getRefundStatus()
    {
        Map<Integer,String> map = new HashMap<>();
        map.put(0,"待处理");
        map.put(1,"财务已确认");
        map.put(2,"退款成功");
        map.put(3,"关闭退款");
        return map;
    }

    public String getRefundStatusName(Integer key)
    {
        return getRefundStatus().get(key);
    }
}

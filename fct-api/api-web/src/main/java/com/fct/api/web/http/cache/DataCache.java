package com.fct.api.web.http.cache;

import com.fct.core.utils.ConvertUtils;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataCache {

    /**
     * 银行列表
     *
     * @return
     */
    public List getBanks() {

        List banks = new ArrayList();
        banks.add("支付宝");
        banks.add("中国银行");
        banks.add("中国工商银行");
        banks.add("中国农业银行");
        banks.add("中国建设银行");
        banks.add("交通银行");
        banks.add("招商银行");
        banks.add("广发银行");
        banks.add("上海浦东发展银行");
        banks.add("中国邮政储蓄银行");

        return banks;
    }

    public Boolean validateBank(String bankName) {
        List banks = this.getBanks();
        return banks.contains(bankName) ? true : false;
    }


    public Map<String, Object> getRechargeRules() {
        Map<String, Object> map = new HashMap<>();
        map.put("min", 100);
        map.put("max", 10000000);
        map.put("defaultGift", 0.2);

        List<Map<String, Object>> lsRuleMaps = new ArrayList<>();
        Map<String, Object> ruleMap = new HashMap<>();
        ruleMap.put("price", 500);
        ruleMap.put("giftPercent", 0.2);
        lsRuleMaps.add(ruleMap);

        ruleMap = new HashMap<>();
        ruleMap.put("price", 1000);
        ruleMap.put("giftPercent", 0.3);
        lsRuleMaps.add(ruleMap);

        ruleMap = new HashMap<>();
        ruleMap.put("price", 3000);
        ruleMap.put("giftPercent", 0.4);
        lsRuleMaps.add(ruleMap);

        ruleMap = new HashMap<>();
        ruleMap.put("price", 5000);
        ruleMap.put("giftPercent", 0.5);
        lsRuleMaps.add(ruleMap);

        ruleMap = new HashMap<>();
        ruleMap.put("price", 10000);
        ruleMap.put("giftPercent", 0.6);
        lsRuleMaps.add(ruleMap);

        ruleMap = new HashMap<>();
        ruleMap.put("price", 50000);
        ruleMap.put("giftPercent", 0.7);
        lsRuleMaps.add(ruleMap);

        map.put("rules", lsRuleMaps);

        return map;
    }

    public BigDecimal getGift(BigDecimal amount) {

        Map<String, Object> map = this.getRechargeRules();
        List<Map<String, Object>> rules = (List<Map<String, Object>>) map.get("rules");
        for (Map<String, Object> rule : rules) {
            if (ConvertUtils.toBigDeciaml(rule.get("price")) == amount) {
                return amount.multiply(ConvertUtils.toBigDeciaml(rule.get("giftPercent")));
            }
        }

        return amount.multiply(ConvertUtils.toBigDeciaml(map.get("defaultGift")));
    }
}

package com.fct.api.web.http.cache;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataCache {

    /**银行列表
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
}

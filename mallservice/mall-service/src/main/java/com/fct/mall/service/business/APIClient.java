package com.fct.mall.service.business;

import com.fct.finance.interfaces.FinanceService;
import com.fct.promotion.interfaces.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jon on 2017/5/16.
 */
public class APIClient {

    @Autowired
    public  static PromotionService promotionService;

    @Autowired
    public  static FinanceService financeService;
}

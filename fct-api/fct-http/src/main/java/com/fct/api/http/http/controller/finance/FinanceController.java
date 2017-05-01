package com.fct.api.http.http.controller.finance;

import com.fct.api.http.http.dto.json.JsonResponseEntity;
import com.fct.finance.data.entity.PayOrder;
import com.fct.finance.interfaces.FinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nick on 2017/5/1.
 */
@RestController
@RequestMapping(value = "/api/finance")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    /**
     * 获取订单
     * @param orderId
     * @return
     */
    @RequestMapping(value = "getOrder", method = RequestMethod.GET)
    public JsonResponseEntity<PayOrder> getPayOrder(@RequestParam String orderId){
        PayOrder order = financeService.getPayOrder(orderId);
        JsonResponseEntity<PayOrder> responseEntity = new JsonResponseEntity<>();
        responseEntity.setData(order);
        return responseEntity;
    }
}

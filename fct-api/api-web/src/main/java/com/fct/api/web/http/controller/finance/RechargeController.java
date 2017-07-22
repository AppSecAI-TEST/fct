package com.fct.api.web.http.controller.finance;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.finance.data.entity.RechargeRecord;
import com.fct.finance.interfaces.FinanceService;
import com.fct.finance.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "/finance/recharges")
public class RechargeController extends BaseController {

    @Autowired
    private FinanceService financeService;

    /**获取用户充值列表
     *
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<RechargeRecord>> findRecharge(Integer page_index, Integer page_size) {

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);

        MemberLogin member = this.memberAuth();

        PageResponse<RechargeRecord> lsRecharge = financeService.findRechargeRecord(member.getMemberId(), "",
                "", "", -1, 0, "", "", page_index, page_size);

        ReturnValue<PageResponse<RechargeRecord>> response = new ReturnValue<>();
        response.setData(lsRecharge);

        return response;
    }

    /**充值详情
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ReturnValue<RechargeRecord> getRecharge(@PathVariable("id") Integer id) {

        id = ConvertUtils.toInteger(id);
        if (id < 1) {

            return  new ReturnValue<>(404, "充值记录不存在");
        }

        MemberLogin member = this.memberAuth();

        RechargeRecord recharge = financeService.getRechargeRecord(id);
        if (recharge != null && !recharge.getMemberId().equals(member.getMemberId())) {

            return new ReturnValue<RechargeRecord>(404, "请求错误");
        }

        ReturnValue<RechargeRecord> response = new ReturnValue<>();
        response.setData(recharge);

        return response;
    }

    /**保存用户充值申请
     *
     * @param pay_amount
     * @param gift_amount
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue saveRecharge(BigDecimal pay_amount, BigDecimal gift_amount) {

        pay_amount = ConvertUtils.toBigDeciaml(pay_amount);
        gift_amount = ConvertUtils.toBigDeciaml(gift_amount);
        if (pay_amount.doubleValue() < 1) {

            return new ReturnValue(404, "充值金额不能小于1000元");
        }


        MemberLogin member = this.memberAuth();

        RechargeRecord recharge = new RechargeRecord();
        recharge.setMemberId(member.getMemberId());
        recharge.setCellPhone(member.getCellPhone());
        recharge.setPayAmount(pay_amount);
        recharge.setGiftAmount(gift_amount);

        financeService.createRechargeRecord(recharge);

        return new ReturnValue(200, "充值成功");
    }

    /**充值申请数据
     *
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ReturnValue<Map<Integer, String>> createRecharge()
    {
        MemberLogin member = this.memberAuth();

        Map<Integer, String> map = new HashMap<>();
        map.put(500, "送500,可得1000元。");
        map.put(1000, "送1000,可得2000元。");
        map.put(2000, "送2000,可得4000元。");
        map.put(3000, "送3000,可得6000元。");
        map.put(5000, "送5000,可得10000元。");
        map.put(6000, "送6000,可得12000元。");
        map.put(8000, "送8000,可得16000元。");
        map.put(10000, "送10000,可得20000元。");

        //充值数据
        ReturnValue<Map<Integer, String>> response = new ReturnValue<>();
        response.setData(map);

        return response;
    }
}

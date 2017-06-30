package com.fct.api.web.http.controller.finance;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.finance.data.entity.RechargeRecord;
import com.fct.finance.interfaces.FinanceService;
import com.fct.finance.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "recharges")
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
    public ReturnValue<PageResponse<RechargeRecord>> findRecharges(Integer page_index, Integer page_size) {

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);

        MemberLogin member = this.memberAuth();

        PageResponse<RechargeRecord> lsRecharge = financeService.findRechargeRecord(member.getMemberId(), "",
                "", "", -1, 0, "", "", page_index, page_size);

        ReturnValue<PageResponse<RechargeRecord>> response = new ReturnValue<>();
        response.setData(lsRecharge);

        return response;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue saveRecharge(BigDecimal pay_amount, BigDecimal gift_amount) {

        pay_amount = ConvertUtils.toBigDeciaml(pay_amount);
        gift_amount = ConvertUtils.toBigDeciaml(gift_amount);

        MemberLogin member = this.memberAuth();

        RechargeRecord recharge = new RechargeRecord();
        recharge.setMemberId(member.getMemberId());
        recharge.setCellPhone(member.getCellPhone());
        recharge.setPayAmount(pay_amount);
        recharge.setGiftAmount(gift_amount);

        financeService.createRechargeRecord(recharge);

        return new ReturnValue(200, "充值成功");
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ReturnValue<List<Map<String, Object>>> createRecharge()
    {
        MemberLogin member = this.memberAuth();

        //充值数据

        return new ReturnValue<List<Map<String, Object>>>();
    }
}

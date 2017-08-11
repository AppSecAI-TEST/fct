package com.fct.api.web.http.controller.finance;

import com.fct.api.web.http.cache.DataCache;
import com.fct.api.web.http.cache.PaymentCache;
import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.finance.data.entity.RechargeRecord;
import com.fct.finance.data.entity.WithdrawRecord;
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

    @Autowired
    private PaymentCache paymentCache;

    @Autowired
    private DataCache dataCache;

    /**获取用户充值列表
     *
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<Map<String, Object>>> findRecharge(Integer page_index, Integer page_size) {

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);

        MemberLogin member = this.memberAuth();

        PageResponse<RechargeRecord> lsRecharge = financeService.findRechargeRecord(member.getMemberId(), "",
                "", "", -1, 0, "", "", page_index, page_size);

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();
        if (lsRecharge != null && lsRecharge.getTotalCount() > 0) {

            List<Map<String, Object>> lsMaps = new ArrayList<>();
            Map<String, Object> map = null;
            for (RechargeRecord recharge: lsRecharge.getElements()) {

                map = new HashMap<>();
                map.put("id", recharge.getId());
                map.put("payAmount", recharge.getPayAmount());
                map.put("giftAmount", recharge.getGiftAmount());
                map.put("amount", recharge.getAmount());
                map.put("payOrderId", recharge.getPayOrderId());
                map.put("status", recharge.getStatus());
                map.put("payName", paymentCache.getNameByCode(recharge.getPayPlatform()));
                map.put("createTime", this.getFormatDate(recharge.getCreateTime()));
                map.put("payTime", this.getFormatDate(recharge.getPayTime()));
                lsMaps.add(map);
            }

            pageMaps.setElements(lsMaps);
            pageMaps.setCurrent(lsRecharge.getCurrent());
            pageMaps.setTotalCount(lsRecharge.getTotalCount());
            pageMaps.setHasMore(lsRecharge.isHasMore());
        }

        ReturnValue<PageResponse<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(pageMaps);

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
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue<Integer> saveRecharge(BigDecimal pay_amount) {

        pay_amount = ConvertUtils.toBigDeciaml(pay_amount);
        if (pay_amount.doubleValue() < 100) {

            return new ReturnValue(404, "充值金额不能小于100元");
        }


        MemberLogin member = this.memberAuth();

        RechargeRecord recharge = new RechargeRecord();
        recharge.setMemberId(member.getMemberId());
        recharge.setCellPhone(member.getCellPhone());
        recharge.setPayAmount(pay_amount);
        recharge.setGiftAmount(dataCache.getGift(pay_amount));

        Integer id = financeService.createRechargeRecord(recharge);
        ReturnValue<Integer> response = new ReturnValue<>();
        response.setData(id);

        return response;
    }

    /**充值申请数据
     *
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> createRecharge()
    {
        MemberLogin member = this.memberAuth();

        //充值数据
        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(dataCache.getRechargeRules());

        return response;
    }
}

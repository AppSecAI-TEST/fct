package com.fct.api.web.http.controller.finance;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.WithdrawRecord;
import com.fct.finance.interfaces.FinanceService;
import com.fct.finance.interfaces.PageResponse;
import com.fct.member.data.entity.MemberBankInfo;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "/finance/withdraws")
public class WithdrawController extends BaseController {

    @Autowired
    private FinanceService financeService;

    /**提现列表
     *
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<WithdrawRecord>> findWithdraw(Integer page_index, Integer page_size) {

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);

        MemberLogin member = this.memberAuth();

        PageResponse<WithdrawRecord> lsWithdraw = financeService.findWithdrawRecord(member.getMemberId(), "",
                -1, "", "", page_index, page_size);

        ReturnValue<PageResponse<WithdrawRecord>> response = new ReturnValue<>();
        response.setData(lsWithdraw);

        return response;
    }

    /**保存用户提现申请
     *
     * @param amount
     * @param remark
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue saveWithdraw(BigDecimal amount, String remark) {

        MemberLogin member = this.memberAuth();

        if (member.getAuthStatus() != 1) {

            return new ReturnValue(404, "还未进行实名认证");
        }

        MemberBankInfo bankInfo = memberService.getMemberBankInfo(member.getMemberId());
        WithdrawRecord withdraw = new WithdrawRecord();
        withdraw.setMemberId(member.getMemberId());
        withdraw.setName(bankInfo.getName());
        withdraw.setBankName(bankInfo.getBankName());
        withdraw.setBankAccount(bankInfo.getBankAccount());
        withdraw.setAmount(amount);
        withdraw.setRemark(remark);

        financeService.applyWithdraw(withdraw);

        return new ReturnValue(200, "申请成功");
    }

    /**申请提现数据
     *
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> createWithdraw() {

        MemberLogin member = this.memberAuth();

        if (member.getAuthStatus() != 1) {

            return new ReturnValue(404, "还未进行实名认证");
        }

        MemberBankInfo bankInfo = memberService.getMemberBankInfo(member.getMemberId());

        MemberAccount account = financeService.getMemberAccount(member.getMemberId());

        Map<String, Object> map = new HashMap<>();

        map.put("name", bankInfo.getName());
        map.put("bankName", bankInfo.getBankName());
        map.put("bankAccount", bankInfo.getBankAccount());
        map.put("withdrawAmount", account.getWithdrawAmount());

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(map);

        return response;
    }
}

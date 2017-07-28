package com.fct.api.web.http.controller.finance;

import com.fct.api.web.http.controller.BaseController;
import com.fct.api.web.utils.MaskUtil;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "/finance/withdraws")
public class WithdrawController extends BaseController {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MaskUtil maskUtil;

    /**提现列表
     *
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<Map<String, Object>>> findWithdraw(Integer page_index, Integer page_size) {

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);

        MemberLogin member = this.memberAuth();
        PageResponse<WithdrawRecord> lsWithdraw = financeService.findWithdrawRecord(member.getMemberId(), "",
                -1, "", "", page_index, page_size);

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();
        if (lsWithdraw != null && lsWithdraw.getTotalCount() > 0) {

            List<Map<String, Object>> lsMaps = new ArrayList<>();
            Map<String, Object> map = null;
            for (WithdrawRecord withdraw: lsWithdraw.getElements()) {

                map = new HashMap<>();
                map.put("bankName", withdraw.getBankName());
                map.put("bankAccount", maskUtil.autoMask(withdraw.getBankAccount()));
                map.put("amount", withdraw.getAmount());
                map.put("statusName", this.getStatusName(withdraw.getStatus()));
                map.put("createTime", this.getFormatDate(withdraw.getCreateTime(), "yyyy.MM.dd"));

                lsMaps.add(map);
            }

            pageMaps.setElements(lsMaps);
            pageMaps.setCurrent(lsWithdraw.getCurrent());
            pageMaps.setTotalCount(lsWithdraw.getTotalCount());
            pageMaps.setHasMore(lsWithdraw.isHasMore());
        }

        ReturnValue<PageResponse<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(pageMaps);

        return response;
    }

    /**保存用户提现申请
     *
     * @param amount
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ReturnValue saveWithdraw(BigDecimal amount) {

        amount = ConvertUtils.toBigDeciaml(amount);
        if (amount.doubleValue() < 100) {
            return new ReturnValue(404, "提现金额不能小于100元");
        }

        MemberLogin member = this.memberAuth();

        if (member.getAuthStatus() != 1) {

            return new ReturnValue(404, "还未进行实名认证");
        }

        MemberBankInfo bankInfo = memberService.getMemberBankInfoByMember(member.getMemberId());
        WithdrawRecord withdraw = new WithdrawRecord();
        withdraw.setMemberId(member.getMemberId());
        withdraw.setCellPhone(member.getCellPhone());
        withdraw.setName(bankInfo.getName());
        withdraw.setBankName(bankInfo.getBankName());
        withdraw.setBankAccount(bankInfo.getBankAccount());
        withdraw.setAmount(amount);

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

        MemberBankInfo bankInfo = memberService.getMemberBankInfoByMember(member.getMemberId());

        MemberAccount account = financeService.getMemberAccount(member.getMemberId());

        Map<String, Object> map = new HashMap<>();

        map.put("name", bankInfo.getName());
        map.put("bankName", bankInfo.getBankName());
        map.put("bankAccount", maskUtil.autoMask(bankInfo.getBankAccount()));
        map.put("withdrawAmount", account.getWithdrawAmount());

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(map);

        return response;
    }

    private String getStatusName(Integer status) {

        switch (status) {
            case 1:
                return "提现成功";
            case 2:
                return "处理失败";
            default:
                return "等待处理";
        }
    }
}

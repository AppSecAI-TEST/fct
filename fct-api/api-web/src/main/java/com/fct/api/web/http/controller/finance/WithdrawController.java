package com.fct.api.web.http.controller.finance;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.finance.data.entity.WithdrawRecord;
import com.fct.finance.interfaces.FinanceService;
import com.fct.finance.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "withdraws")
public class WithdrawController extends BaseController {

    @Autowired
    private FinanceService financeService;

    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<PageResponse<WithdrawRecord>> findWithdraws(Integer page_index, Integer page_size) {

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size);

        MemberLogin member = this.memberAuth();

        PageResponse<WithdrawRecord> lsWithdraw = financeService.findWithdrawRecord(member.getMemberId(), "",
                -1, "", "", page_index, page_size);

        ReturnValue<PageResponse<WithdrawRecord>> response = new ReturnValue<>();
        response.setData(lsWithdraw);

        return response;
    }
}

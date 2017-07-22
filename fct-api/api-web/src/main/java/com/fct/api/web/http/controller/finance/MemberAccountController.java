package com.fct.api.web.http.controller.finance;

import com.fct.api.web.http.controller.BaseController;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.ReturnValue;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.MemberAccountHistory;
import com.fct.finance.interfaces.FinanceService;
import com.fct.finance.interfaces.PageResponse;
import com.fct.member.data.entity.MemberLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/finance/member/account")
public class MemberAccountController extends BaseController {

    @Autowired
    private FinanceService financeService;

    /**用户帐户明细
     *
     * @param page_index
     * @param page_size
     * @return
     */
    @RequestMapping(value = "logs", method = RequestMethod.GET)
    public ReturnValue<PageResponse<Map<String, Object>>> findAccountLogs(Integer page_index, Integer page_size) {

        page_index = ConvertUtils.toPageIndex(page_index);
        page_size = ConvertUtils.toInteger(page_size, 20);

        MemberLogin member = this.memberAuth();

        PageResponse<MemberAccountHistory> pageResponse =
                financeService.findMemberAccountHistory(member.getMemberId(), "", "", "",
                        "", "", page_index, page_size);

        PageResponse<Map<String, Object>> pageMaps = new PageResponse<>();
        if (pageResponse != null)
        {
            if (pageResponse.getTotalCount() > 0) {

                List<Map<String, Object>> lsMaps = new ArrayList<>();
                Map<String, Object> map = null;
                for (MemberAccountHistory history: pageResponse.getElements()) {

                    map = new HashMap<>();
                    map.put("remark", history.getRemark());
                    map.put("balanceAmount", history.getBalanceAmount());
                    map.put("amount", history.getAmount());
                    map.put("createTime", DateUtils.formatDate(history.getCreateTime(), "yyyy-MM-dd HH:mm"));
                    lsMaps.add(map);
                }

                pageMaps.setElements(lsMaps);
                pageMaps.setCurrent(pageResponse.getCurrent());
                pageMaps.setTotalCount(pageResponse.getTotalCount());
                pageMaps.setHasMore(pageResponse.isHasMore());
            }
        }

        ReturnValue<PageResponse<Map<String, Object>>> response = new ReturnValue<>();
        response.setData(pageMaps);

        return response;
    }

    /**获取用户帐户
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ReturnValue<Map<String, Object>> getAccount() {

        MemberLogin member = this.memberAuth();
        MemberAccount account = financeService.getMemberAccount(member.getMemberId());
        Map<String, Object> map = new HashMap<>();

        if (account != null) {
            map.put("points", account.getPoints());
            map.put("availableAmount", account.getAvailableAmount());
            map.put("withdrawAmount", account.getWithdrawAmount());
        } else {
            map.put("points", 0);
            map.put("availableAmount", 0);
            map.put("withdrawAmount", 0);
        }

        ReturnValue<Map<String, Object>> response = new ReturnValue<>();
        response.setData(map);

        return response;
    }



}

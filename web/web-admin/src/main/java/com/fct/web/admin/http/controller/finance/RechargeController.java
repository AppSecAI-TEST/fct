package com.fct.web.admin.http.controller.finance;

import com.fct.common.utils.ConvertUtils;
import com.fct.common.utils.PageUtil;
import com.fct.finance.data.entity.RechargeRecord;
import com.fct.finance.interfaces.FinanceService;
import com.fct.finance.interfaces.PageResponse;
import com.fct.web.admin.http.cache.CacheFinanceManager;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/finance")
public class RechargeController {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private CacheFinanceManager cacheFinanceManager;

    @RequestMapping(value = "/recharge", method = RequestMethod.GET)
    public String index(Integer memberid, String cellphone, String payplatform,String payorderid,
                        Integer status,Integer timetype,String starttime, String endtime,
                        Integer page,Model model) {

        page = ConvertUtils.toPageIndex(page);
        memberid =ConvertUtils.toInteger(memberid);
        status = ConvertUtils.toInteger(status,-1);
        payorderid =ConvertUtils.toString(payorderid);
        payplatform =ConvertUtils.toString(payplatform);
        timetype =ConvertUtils.toInteger(timetype);
        starttime =ConvertUtils.toString(starttime);
        endtime =ConvertUtils.toString(endtime);
        cellphone = ConvertUtils.toString(cellphone);

        Integer pagesize = 30;
        String pageUrl = "?page=%d";

        if(status>-1)
        {
            pageUrl+="&status="+status;
        }
        if(memberid>0)
        {
            pageUrl+="&memberid="+memberid;
        }
        if(timetype>0)
        {
            pageUrl+="&timetype="+timetype;
        }
        if(!StringUtils.isEmpty(payorderid))
        {
            pageUrl += "&payorderid="+payorderid;
        }
        if(!StringUtils.isEmpty(payplatform))
        {
            pageUrl += "&payplatform="+payplatform;
        }
        if(!StringUtils.isEmpty(cellphone))
        {
            pageUrl += "&cellphone="+cellphone;
        }
        if(!StringUtils.isEmpty(starttime))
        {
            pageUrl += "&starttime="+starttime;
        }
        if(!StringUtils.isEmpty(endtime))
        {
            pageUrl += "&endtime="+endtime;
        }

        PageResponse<RechargeRecord> pageResponse = null;
        try {
            pageResponse = financeService.findRechargeRecord(memberid, cellphone, payplatform,payorderid,
                    status,timetype,starttime, endtime,page,pagesize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<RechargeRecord>();
        }

        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pagesize,pageUrl));


        Map<String,Object> query = new HashMap<>();
        query.put("status", status);
        query.put("cellphone", cellphone);
        query.put("payplatform", payplatform);
        query.put("payorderid", payorderid);
        query.put("timetype", timetype);
        query.put("starttime", starttime);
        query.put("endtime", endtime);

        model.addAttribute("cacheFinance", cacheFinanceManager);
        model.addAttribute("query", query);
        model.addAttribute("lsRecharge", pageResponse.getElements());

        return "finance/recharge";
    }
}
package com.fct.web.admin.http.controller.finance;

import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.finance.data.entity.PayOrder;
import com.fct.finance.interfaces.FinanceService;
import com.fct.finance.interfaces.PageResponse;
import com.fct.web.admin.http.cache.CacheFinanceManager;
import com.fct.web.admin.http.controller.BaseController;
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
@RequestMapping(value = "/finance/payorder")
public class PayOrderController extends BaseController {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private CacheFinanceManager cacheFinanceManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Integer memberid, String selkey,String selvalue,String payplatform,String payorderid,String tradeid,
                        String tradetype,Integer status,Integer timetype,String starttime, String endtime,
                        Integer page,Model model) {

        page = ConvertUtils.toPageIndex(page);
        memberid =ConvertUtils.toInteger(memberid);
        status = ConvertUtils.toInteger(status,-1);
        payorderid =ConvertUtils.toString(payorderid);
        payplatform =ConvertUtils.toString(payplatform);
        timetype =ConvertUtils.toInteger(timetype);
        starttime =ConvertUtils.toString(starttime);
        endtime =ConvertUtils.toString(endtime);
        selkey = ConvertUtils.toString(selkey);
        selvalue = ConvertUtils.toString(selvalue);
        tradeid = ConvertUtils.toString(tradeid);
        tradetype =ConvertUtils.toString(tradetype);

        Integer pagesize = 30;

        StringBuilder sb = new StringBuilder();
        sb.append("?page=%d");

        String orderid = selvalue;
        String cellphone ="";
        if(selkey == "cellphone")
        {
            cellphone = selvalue;
        }

        if(status>-1)
        {
            sb.append("&status="+status);
        }
        if(memberid>0)
        {
            sb.append("&memberid="+memberid);
        }
        if(timetype>0)
        {
            sb.append("&timetype="+timetype);
        }
        if(!StringUtils.isEmpty(payorderid))
        {
            sb.append("&payorderid="+payorderid);
        }
        if(!StringUtils.isEmpty(payplatform))
        {
            sb.append("&payplatform="+payplatform);
        }
        if(!StringUtils.isEmpty(cellphone))
        {
            sb.append("&cellphone="+cellphone);
        }
        if(!StringUtils.isEmpty(starttime))
        {
            sb.append("&starttime="+starttime);
        }
        if(!StringUtils.isEmpty(endtime))
        {
            sb.append("&endtime="+endtime);
        }
        if(!StringUtils.isEmpty(tradeid))
        {
            sb.append("&tradeid="+tradeid);
        }
        if(!StringUtils.isEmpty(tradetype))
        {
            sb.append("&tradetype="+tradetype);
        }

        PageResponse<PayOrder> pageResponse = null;
        try {
            pageResponse = financeService.findPayRecord(memberid,cellphone,orderid,payplatform,payorderid,tradeid,tradetype,
                   status,timetype,starttime, endtime,page,pagesize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<PayOrder>();
        }

        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pagesize,sb.toString()));


        Map<String,Object> query = new HashMap<>();
        query.put("status", status);
        query.put("selkey", selkey);
        query.put("selvalue", selvalue);
        query.put("payplatform", payplatform);
        query.put("payorderid", payorderid);
        query.put("tradetype", tradetype);
        query.put("tradeid", tradeid);
        query.put("timetype", timetype);
        query.put("starttime", starttime);
        query.put("endtime", endtime);

        model.addAttribute("cacheFinance", cacheFinanceManager);
        model.addAttribute("query", query);
        model.addAttribute("lsPayOrder", pageResponse.getElements());

        return "finance/payorder/index";
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String detail(String orderid, Model model) {

        orderid = ConvertUtils.toString(orderid);
        PayOrder payOrder = null;
        try
        {
            payOrder = financeService.getPayOrder(orderid);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }

        model.addAttribute("cacheFinance", cacheFinanceManager);
        model.addAttribute("pay", payOrder);

        return "finance/payorder/detail";
    }
}

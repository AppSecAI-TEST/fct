package com.fct.web.admin.http.controller.finance;

import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.finance.data.entity.MemberAccount;
import com.fct.finance.data.entity.MemberAccountHistory;
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
@RequestMapping(value = "/finance/account")
public class AccountController extends BaseController{

    @Autowired
    private FinanceService financeService;

    @Autowired
    private CacheFinanceManager cacheFinanceManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Integer orderby,String cellphone,Integer page,Model model) {

        page =ConvertUtils.toPageIndex(page);
        orderby = ConvertUtils.toInteger(orderby);
        cellphone = ConvertUtils.toString(cellphone);

        Integer pagesize = 30;
        String pageUrl = "?page=%d";

        if(orderby>0)
        {
            pageUrl+="&orderby="+orderby;
        }
        if(!StringUtils.isEmpty(cellphone))
        {
            pageUrl += "&cellphone="+cellphone;
        }

        PageResponse<MemberAccount> pageResponse = null;
        try {
            pageResponse = financeService.findMemberAccount(cellphone,orderby,page,pagesize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<MemberAccount>();
        }

        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pagesize,pageUrl));


        Map<String,Object> query = new HashMap<>();
        query.put("orderby", orderby);
        query.put("cellphone", cellphone);

        model.addAttribute("query", query);
        model.addAttribute("lsAccount", pageResponse.getElements());

        return "finance/account/index";
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public String history(Integer memberid, String cellphone, String tradeid, String tradetype,
                          String starttime,String endtime,Integer page,Model model) {

        page =ConvertUtils.toPageIndex(page);
        cellphone = ConvertUtils.toString(cellphone);
        memberid = ConvertUtils.toInteger(memberid);
        tradeid =ConvertUtils.toString(tradeid);
        tradetype =ConvertUtils.toString(tradetype);
        starttime =ConvertUtils.toString(starttime);
        endtime =ConvertUtils.toString(endtime);

        Integer pagesize = 30;
        String pageUrl = "?page=%d";

        if(memberid>0)
        {
            pageUrl+="&memberid="+memberid;
        }
        if(!StringUtils.isEmpty(cellphone))
        {
            pageUrl += "&cellphone="+cellphone;
        }

        if(!StringUtils.isEmpty(tradeid))
        {
            pageUrl += "&tradeid="+tradeid;
        }
        if(!StringUtils.isEmpty(tradetype))
        {
            pageUrl += "&tradetype="+tradetype;
        }
        if(!StringUtils.isEmpty(starttime))
        {
            pageUrl += "&starttime="+starttime;
        }
        if(!StringUtils.isEmpty(endtime))
        {
            pageUrl += "&endtime="+endtime;
        }

        PageResponse<MemberAccountHistory> pageResponse = null;
        try {
            pageResponse = financeService.findMemberAccountHistory(memberid,cellphone,tradeid,tradetype,starttime,
                    endtime,page,pagesize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<MemberAccountHistory>();
        }

        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pagesize,pageUrl));


        Map<String,Object> query = new HashMap<>();
        query.put("cellphone", cellphone);
        query.put("tradeid", cellphone);
        query.put("tradetype", cellphone);
        query.put("starttime", starttime);
        query.put("endtime", endtime);

        model.addAttribute("cacheFinance", cacheFinanceManager);
        model.addAttribute("query", query);
        model.addAttribute("lsHistory", pageResponse.getElements());

        return "finance/account/history";
    }

}

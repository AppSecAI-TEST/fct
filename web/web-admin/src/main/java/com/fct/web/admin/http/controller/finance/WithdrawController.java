package com.fct.web.admin.http.controller.finance;

import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.finance.data.entity.WithdrawRecord;
import com.fct.finance.interfaces.FinanceService;
import com.fct.finance.interfaces.PageResponse;
import com.fct.web.admin.http.cache.CacheFinanceManager;
import com.fct.core.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/finance")
public class WithdrawController {
    @Autowired
    private FinanceService financeService;

    @Autowired
    private CacheFinanceManager cacheFinanceManager;

    @RequestMapping(value = "/withdraw", method = RequestMethod.GET)
    public String index(Integer memberid, String cellphone, Integer status,
                        String starttime, String endtime,Integer page,Model model) {

        page = ConvertUtils.toPageIndex(page);
        memberid =ConvertUtils.toInteger(memberid);
        status = ConvertUtils.toInteger(status,-1);
        cellphone = ConvertUtils.toString(cellphone);
        starttime =ConvertUtils.toString(starttime);
        endtime =ConvertUtils.toString(endtime);

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

        PageResponse<WithdrawRecord> pageResponse = null;
        try {
            pageResponse = financeService.findWithdrawRecord(memberid,cellphone, status,
                    starttime, endtime,page,pagesize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<WithdrawRecord>();
        }

        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pagesize,pageUrl));


        Map<String,Object> query = new HashMap<>();
        query.put("status", status);
        query.put("cellphone", cellphone);
        query.put("starttime", starttime);
        query.put("endtime", endtime);

        model.addAttribute("cacheFinance", cacheFinanceManager);
        model.addAttribute("query", query);
        model.addAttribute("lsWithdraw", pageResponse.getElements());

        return "finance/withdraw";
    }

    @RequestMapping(value = "/withdraw/upstatus", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String upStatus(Integer id, String action, String remark)
    {
        id = ConvertUtils.toInteger(id);
        action = ConvertUtils.toString(action);
        remark = ConvertUtils.toString(remark);

        try {
            remark = URLDecoder.decode(remark, "UTF-8");
            switch (action)
            {
                case "fail":
                    financeService.withdrawFail(0,id,remark);
                    break;
                case "success":
                    financeService.withdrawSuccess(0,id);
                    break;
            }
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            //这里没有写进文件
            Constants.logger.error(exp.toString());
            return AjaxUtil.alert("系统或网络错误，请稍候再试。");
        }

        return AjaxUtil.reload("处理提现数据成功。");
    }
}

package com.fct.web.admin.http.controller.finance;

import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.finance.data.entity.SettleRecord;
import com.fct.finance.interfaces.FinanceService;
import com.fct.finance.interfaces.PageResponse;
import com.fct.web.admin.http.cache.CacheFinanceManager;
import com.fct.core.utils.AjaxUtil;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/finance")
public class SettleController extends BaseController{

    @Autowired
    private FinanceService financeService;

    @Autowired
    private CacheFinanceManager cacheFinanceManager;

    @RequestMapping(value = "/settle", method = RequestMethod.GET)
    public String index(Integer memberid, String selkey, String selvalue, Integer status,
                        String starttime, String endtime,Integer page,Model model) {

        page = ConvertUtils.toPageIndex(page);
        memberid =ConvertUtils.toInteger(memberid);
        status = ConvertUtils.toInteger(status,-1);
        selkey =ConvertUtils.toString(selkey);
        selvalue =ConvertUtils.toString(selvalue);
        starttime =ConvertUtils.toString(starttime);
        endtime =ConvertUtils.toString(endtime);

        Integer pagesize = 30;
        String pageUrl = "?page=%d";

        String cellphone ="";
        String tradeid ="";
        if(selkey.equals("cellphone"))
        {
            cellphone = selvalue;
        }
        else{
            tradeid = selvalue;
        }
        if(status>-1)
        {
            pageUrl+="&status="+status;
        }
        if(memberid>0)
        {
            pageUrl+="&memberid="+memberid;
        }
        if(!StringUtils.isEmpty(selvalue))
        {
            pageUrl += "&selkey="+ selkey +"&selvalue="+selvalue;
        }
        if(!StringUtils.isEmpty(starttime))
        {
            pageUrl += "&starttime="+starttime;
        }
        if(!StringUtils.isEmpty(endtime))
        {
            pageUrl += "&endtime="+endtime;
        }

        PageResponse<SettleRecord> pageResponse = null;
        try {
            pageResponse = financeService.findSettleRecord(memberid,cellphone,"",tradeid, status,
                    starttime, endtime,page,pagesize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<SettleRecord>();
        }

        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pagesize,pageUrl));


        Map<String,Object> query = new HashMap<>();
        query.put("status", status);
        query.put("selkey", selkey);
        query.put("selvalue", selvalue);
        query.put("starttime", starttime);
        query.put("endtime", endtime);

        model.addAttribute("cacheFinance", cacheFinanceManager);
        model.addAttribute("query", query);
        model.addAttribute("lsSettle", pageResponse.getElements());

        return "finance/settle";
    }

    @RequestMapping(value = "/settle/upstatus", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String upStatus(String ids, String action, @RequestParam(required = false) String checkbox,String remark)
    {
        ids = ConvertUtils.toString(ids);
        action = ConvertUtils.toString(action);
        remark =ConvertUtils.toString(remark);
        checkbox =ConvertUtils.toString(checkbox);
        if(!StringUtils.isEmpty(checkbox))
        {
            ids = checkbox;
        }

        try {
            remark = URLDecoder.decode(remark, "UTF-8");
            switch (action)
            {
                case "refuse":
                    financeService.settleRefuse(0,Integer.parseInt(ids),remark);
                    break;
                case "pass":
                    financeService.settleConfirm(0,ids);
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

        return AjaxUtil.reload("处理结算数据成功。");
    }
}

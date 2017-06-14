package com.fct.web.admin.http.controller.order;

import com.fct.common.exceptions.Exceptions;
import com.fct.common.utils.ConvertUtils;
import com.fct.common.utils.PageUtil;
import com.fct.finance.data.entity.RefundRecord;
import com.fct.finance.interfaces.FinanceService;
import com.fct.mall.data.entity.OrderRefund;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.OrderRefundDTO;
import com.fct.mall.interfaces.PageResponse;
import com.fct.web.admin.http.cache.CacheOrderManager;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jon on 2017/6/11.
 */
@Controller
@RequestMapping(value = "/order/refund")
public class RefundController extends BaseController{

    @Autowired
    private MallService mallService;

    @Autowired
    private FinanceService financeService;

    @Autowired
    private CacheOrderManager cacheOrderManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String selkey,String selvalue, Integer status,String starttime, String endtime,Integer page,Model model)
    {
        selkey = ConvertUtils.toString(selkey);
        selvalue = ConvertUtils.toString(selvalue);
        status = ConvertUtils.toInteger(status,-1);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);
        page =ConvertUtils.toPageIndex(page);

        Integer pagesize = 30;
        String pageUrl = "?page=%d";

        String goodsname="";
        String orderid="";
        if(selkey == "orderid")
        {
            orderid = selvalue;
        }
        else
        {
            goodsname = selvalue;
        }

        if(!StringUtils.isEmpty(selvalue))
        {
            pageUrl += "&selkey="+selkey +"&selvalue="+selvalue;
        }
        if(status>-1)
        {
            pageUrl+="&status="+status;
        }
        if(!StringUtils.isEmpty(selvalue))
        {
            pageUrl += "&selkey="+selkey +"&selvalue="+selvalue;
        }
        if(!StringUtils.isEmpty(starttime))
        {
            pageUrl += "&starttime="+starttime;
        }
        if(!StringUtils.isEmpty(endtime))
        {
            pageUrl += "&endtime="+endtime;
        }


        PageResponse<OrderRefundDTO> pageResponse = null;
        try {
            pageResponse = mallService.findOrderRefund(orderid,goodsname,0,0,status,starttime, endtime, page, pagesize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<OrderRefundDTO>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("selkey", selkey);
        query.put("selvalue", selvalue);
        query.put("status", status);
        query.put("starttime",starttime);
        query.put("endtime",endtime);

        model.addAttribute("query", query);
        model.addAttribute("lsRefund", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pagesize,pageUrl));

        model.addAttribute("cacheOrder",cacheOrderManager);

        return "/order/refund/index";
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String detail(Integer id,Model model)
    {
        id = ConvertUtils.toInteger(id);
        OrderRefund orderRefund = null;

        RefundRecord refundRecord = null;
        try
        {
            orderRefund = mallService.getOrderRefund(id);
            if(orderRefund.getStatus() ==3 || orderRefund.getStatus()==4) {
                refundRecord = financeService.getRefundRecordByTrade("buy", id.toString());
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        if(orderRefund == null)
            orderRefund = new OrderRefund();

        model.addAttribute("cacheOrder",cacheOrderManager);
        model.addAttribute("refund",orderRefund);
        model.addAttribute("record",refundRecord);

        return "/order/refund/detail";
    }

    @RequestMapping(value = "/handle", method = RequestMethod.GET)
    public String handle(Integer id,String action,Model model)
    {
        id = ConvertUtils.toInteger(id);
        action = ConvertUtils.toString(action);

        OrderRefund orderRefund = null;
        try {
            orderRefund = mallService.getOrderRefund(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            orderRefund = new OrderRefund();
        }

        model.addAttribute("cacheOrder",cacheOrderManager);
        model.addAttribute("refund",orderRefund);
        model.addAttribute("action",action);

        return "/order/refund/handle";
    }

    @RequestMapping(value = "/savehandle", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String saveHandle(Integer id,String action,String img,String description,Integer refundmethod)
    {
        id = ConvertUtils.toInteger(id);
        action = ConvertUtils.toString(action);
        description = ConvertUtils.toString(description);
        refundmethod = ConvertUtils.toInteger(refundmethod);
        img = ConvertUtils.toString(img);

        try {
            mallService.handleOrderRefund(action,0,id,refundmethod,description,img,1);
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.remind(exp.getMessage());
        }
        catch (Exception exp)
        {
            //这里没有写进文件
            Constants.logger.error(Exceptions.getStackTraceAsString(exp));
            return AjaxUtil.remind("系统或网络错误，请稍候再试。");
        }

        return AjaxUtil.reload("处理退款状态成功。");
    }
}

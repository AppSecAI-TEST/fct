package com.fct.web.admin.http.controller.order;

import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import com.fct.web.admin.http.cache.CacheOrderManager;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.core.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jon on 2017/6/11.
 */
@Controller
@RequestMapping(value = "/order")
public class OrderController  extends BaseController {

    @Autowired
    private MallService mallService;

    @Autowired
    private CacheOrderManager cacheOrderManager;

    /**
     * 获取订单
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String selkey,String selvalue,Integer shopid,String goodsname,
                        Integer status,String payplatform,String payorderid,Integer timetype,String starttime,
                        String endtime,Integer page,Model model)
    {
        selkey = ConvertUtils.toString(selkey);
        selvalue = ConvertUtils.toString(selvalue);
        shopid =ConvertUtils.toInteger(shopid);
        goodsname =ConvertUtils.toString(goodsname);
        status = ConvertUtils.toInteger(status,-1);
        payplatform =ConvertUtils.toString(payplatform);
        payorderid = ConvertUtils.toString(payorderid);
        timetype = ConvertUtils.toInteger(timetype);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);
        page =ConvertUtils.toPageIndex(page);

        String cellphone="";
        String orderid="";
        String tabname ="order_index";
        Integer pagesize = 30;
        String pageUrl = "?page=%d";

        if(selkey == "orderid")
        {
            orderid = selvalue;
        }
        else
        {
            cellphone = selvalue;
        }
        if(status==0)
        {
            tabname = "order_waitpay";
        }
        else if(status==1)
        {
            tabname = "order_paysuccess";
        }

        PageResponse<Orders> pageResponse = null;
        try {
            pageResponse = mallService.findOrders(0, cellphone, orderid, shopid, goodsname,
                    status, payplatform, payorderid, timetype, starttime, endtime, page, pagesize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<Orders>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("selkey", selkey);
        query.put("selvalue", selvalue);
        query.put("shopid", shopid);
        query.put("goodsname", goodsname);
        query.put("status", status);
        query.put("payplatform", payplatform);
        query.put("payorderid",payorderid);
        query.put("timetype",timetype);
        query.put("starttime",starttime);
        query.put("endtime",endtime);

        model.addAttribute("tabname", tabname);
        model.addAttribute("query", query);
        model.addAttribute("lsOrder", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pagesize,pageUrl));

        model.addAttribute("cacheOrder",cacheOrderManager);

        return "/order/index";
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String detail(String orderid,Model model)
    {
        orderid = ConvertUtils.toString(orderid);
        Orders orders = null;

        try
        {
            orders = mallService.getOrders(orderid);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            orders = new Orders();//跳转到错误页
        }
        model.addAttribute("orders",orders);
        model.addAttribute("cacheOrder",cacheOrderManager);

        return "/order/detail";
    }



    @RequestMapping(value = "/upstatus", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String upStatus(String orderid,Integer memberid, String action)
    {
        orderid = ConvertUtils.toString(orderid);
        action = ConvertUtils.toString(action);
        memberid =ConvertUtils.toInteger(memberid);

        try
        {
            switch (action)
            {
                case "pay":
                    mallService.offPaySuccess(orderid,"fct_offline",1);
                    break;
                case "close":
                    mallService.cancelOrders(orderid,memberid,1);
                    break;
            }
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return AjaxUtil.alert("系统或网络错误，请稍候再试。");
        }
        return AjaxUtil.reload("订单操作成功");
    }

    @RequestMapping(value = "/delaytime", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String saveDelaytime(String orderid,Integer password, String action)
    {
        orderid = ConvertUtils.toString(orderid);
        action = ConvertUtils.toString(action);
        password =ConvertUtils.toInteger(password);

        try
        {
            switch (action)
            {
                case "finish":
                    //mallService.delayFinishTime(orderid,"fct_offline",1);
                    break;
                case "close":
                    mallService.delayExpiresTime(orderid,password,1);
                    break;
            }
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return AjaxUtil.alert("系统或网络错误，请稍候再试。");
        }
        return AjaxUtil.reload("延期操作成功");
    }
}



package com.fct.web.admin.http.controller.order;

import com.fct.core.utils.ConvertUtils;
import com.fct.mall.interfaces.MallService;
import com.fct.core.utils.AjaxUtil;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jon on 2017/6/13.
 */
@Controller
@RequestMapping(value = "/order/delivery")
public class DeliveryController extends BaseController {

    @Autowired
    private MallService mallService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String delivery(String orderid,Model model)
    {
        orderid = ConvertUtils.toString(orderid);
        model.addAttribute("orderid",orderid);

        return "/order/delivery";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String saveDelivery(String orderid,String express,String expressno)
    {
        orderid = ConvertUtils.toString(orderid);
        express = ConvertUtils.toString(express);
        expressno =ConvertUtils.toString(expressno);

        try {
            mallService.orderDeliver(orderid, express, expressno, 1);
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.remind(exp.getMessage());
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return AjaxUtil.remind("系统或网络错误，请稍候再试。");
        }
        return AjaxUtil.reload("订单发货成功");

    }
}

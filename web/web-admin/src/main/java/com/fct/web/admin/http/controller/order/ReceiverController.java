package com.fct.web.admin.http.controller.order;

import com.fct.core.utils.ConvertUtils;
import com.fct.mall.data.entity.OrderReceiver;
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
@RequestMapping(value = "/order/receiver")
public class ReceiverController extends BaseController {

    @Autowired
    private MallService mallService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String detail(String orderid,Model model)
    {
        orderid = ConvertUtils.toString(orderid);
        OrderReceiver receiver = null;
        try {
            receiver = mallService.getOrderReceiver(orderid);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            receiver = new OrderReceiver();
        }

        model.addAttribute("receiver",receiver);

        return "/order/receiver";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(String orderid,String name,String phone,String province,String city,String region,String address)
    {
        orderid = ConvertUtils.toString(orderid);
        name = ConvertUtils.toString(name);
        province =ConvertUtils.toString(province);
        city =ConvertUtils.toString(city);
        phone = ConvertUtils.toString(phone);
        region=ConvertUtils.toString(region);
        address =ConvertUtils.toString(address);

        try {
            OrderReceiver receiver = mallService.getOrderReceiver(orderid);
            receiver.setName(name);
            receiver.setPhone(phone);
            receiver.setProvince(province);
            receiver.setCity(city);
            receiver.setRegion(region);
            receiver.setAddress(address);

            mallService.saveOrderReciver(receiver);
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
        return AjaxUtil.reload("修改发货信息成功");

    }
}

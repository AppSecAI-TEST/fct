package com.fct.web.pay.http.controller;

import com.fct.core.utils.AjaxUtil;
import com.fct.finance.data.entity.PayOrder;
import com.fct.finance.interfaces.FinanceService;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.interfaces.MallService;
import com.fct.pay.interfaces.MobilePayService;
import com.fct.web.pay.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.util.Date;

@RequestMapping(value = "mobile")
public class MobileController {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MobilePayService mobilePayService;

    @Autowired
    private MallService mallService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Integer memberid,String tradeid,String tradetype,String payplatform,
                        String openid, Model model) {

        String payurl = "";

        PayOrder payOrder = new PayOrder();

        try
        {
            String cellphone ="";
            BigDecimal accountAmount =new BigDecimal(0);
            BigDecimal payAmount = new BigDecimal(0);
            BigDecimal totalAmount = new BigDecimal(0);
            BigDecimal discountAmount = new BigDecimal(0);
            Integer points =0;
            String desc="";
            Date expiredTime = new Date();
            String showUrl="";
            switch (tradetype)
            {
                case "buy":
                    Orders orders = mallService.getOrders(tradeid);
                    if(orders.getMemberId()!=memberid)
                    {

                    }
                    orders.setPayPlatform(payplatform);
                    //更新订单支付方式。
                    cellphone = orders.getCellPhone();
                    accountAmount = orders.getAccountAmount();
                    payAmount = orders.getPayAmount();
                    totalAmount = orders.getTotalAmount();
                    points = orders.getPoints();
                    desc="购买"+orders.getOrderGoods().get(0).getName();
                    if(orders.getOrderGoods().size()>1)
                    {
                        desc += "等多件";
                    }
                    showUrl = "http://www.fangcuntang.com/my/order/detail?orderid="+orders;
                    expiredTime = orders.getExpiresTime();
                    break;
            }

            String callback = "http://pay.fangcuntang.com/mobile/success?tradeid="+tradeid+"&tradetype="+tradetype;

            payOrder.setCellPhone(cellphone);
            payOrder.setMemberId(memberid);
            payOrder.setTradeId(tradeid);
            payOrder.setTradeType(tradetype);
            payOrder.setAccountAmount(accountAmount);
            payOrder.setPayAmount(payAmount);
            payOrder.setTotalAmount(totalAmount);
            payOrder.setDiscountAmount(discountAmount);
            payOrder.setPoints(points);
            payOrder.setDesc(desc);
            payOrder.setExpiredTime(expiredTime);
            payOrder.setPayPlatform(payplatform);
            payOrder.setShowUrl(showUrl);
            payOrder.setCallbackUrl(callback);

            payOrder = financeService.createPayOrder(payOrder);
            if(payOrder != null)
            {
                switch (payplatform)
                {
                    case "alipay_fctwap":
                        /*retUrl = APIClient.PayMobileService.AlipayCreatePayUrl(payMethod,record.OrderNo, record.CashAmount, record.MemberId.ToString(), record.Description, "",
                                payExpire.ConvertToInt32(0), "", showUrl, "");
                        break;
                    case "unionpay_fctwap":
                        retUrl = APIClient.PayMobileService.UnionpayCreatePayUrl(record.OrderNo, record.CashAmount, record.MemberId.ToString(),record.Description,
                                payExpire.ConvertToInt32(0), "", "");
                        break;*/
                    case "wxpay_fctwap":
                       payurl =  mobilePayService.wxpayWap(payOrder.getOrderId(),openid,payOrder.getPayAmount(),
                               payOrder.getDesc(),"","userip", payOrder.getExpiredTime());
                }
            }
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }

        if(StringUtils.isEmpty(payurl))
        {
            return AjaxUtil.alert("访问出错！由于系统或网络造成的原因，请稍候再试。");
        }

        if (payplatform.equals("alipay_fctwap"))
        {
            return "redirect:"+payurl;
        }

        model.addAttribute("platform",payplatform);
        model.addAttribute("orderid",payOrder.getOrderId());
        model.addAttribute("payurl",payurl);
        return "/mobile/index";
    }
}

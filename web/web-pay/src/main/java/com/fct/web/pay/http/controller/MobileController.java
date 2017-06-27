package com.fct.web.pay.http.controller;

import com.fct.core.utils.AjaxUtil;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.HttpUtils;
import com.fct.finance.data.entity.PayOrder;
import com.fct.finance.interfaces.FinanceService;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.interfaces.MallService;
import com.fct.pay.interfaces.MobilePayService;
import com.fct.web.pay.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
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

    /***
     *
     * 支付选择
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String tradeid, String tradetype, Model model)
    {
        tradeid = ConvertUtils.toString(tradeid);
        tradetype =ConvertUtils.toString(tradetype);
        BigDecimal payAmount =new BigDecimal(0);

        try {
            switch (tradetype) {
                case "buy":
                    Orders orders = mallService.getOrders(tradeid);
                    if (orders.getStatus() != 0) {
                        //出错跳转
                        return "redirect:/error?msg=支付业务订单状态异常";
                    }
                    if (orders.getMemberId() != 100) {
                        //出错跳转
                        return "redirect:/error?msg=非法用户操作";
                    }
            }

            model.addAttribute("platformList", financeService.findPayPlatform());
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return "redirect:/error?msg=系统异常";
        }

        model.addAttribute("payamount", payAmount);
        model.addAttribute("tradeid", tradeid);
        model.addAttribute("tradetype", tradetype);

        return "mobile/index";
    }

    /**
     * 支付保存
     * */
    @RequestMapping(value = "/savepay", method = RequestMethod.POST)
    public String savePay(HttpServletRequest request,String tradeid, String tradetype, String platform,
                                               String openid) {

        tradeid = ConvertUtils.toString(tradeid);
        tradetype =ConvertUtils.toString(tradetype);
        platform = ConvertUtils.toString(platform);

        String payurl = "";

        PayOrder payOrder = new PayOrder();

        try
        {
            String cellphone ="";
            BigDecimal accountAmount =new BigDecimal(0);
            BigDecimal payAmount = new BigDecimal(0);
            BigDecimal totalAmount = new BigDecimal(0);
            BigDecimal discountAmount = new BigDecimal(0);
            Integer memberid = 0;
            Integer points =0;
            String desc="";
            Date expiredTime = new Date();
            String showUrl="";
            switch (tradetype)
            {
                case "buy":
                    Orders orders = mallService.getOrders(tradeid);
                    if(orders == null || orders.getStatus() !=0)
                    {
                        return AjaxUtil.alert("支付业务订单状态异常。");
                    }
                    if(orders.getMemberId() != 100) {
                        return AjaxUtil.alert("非法用户操作。");
                    }
                    orders.setPayPlatform(platform);
                    //保存支付方式
                    memberid = orders.getMemberId();
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
                    showUrl = Constants.domain+"/my/order/detail?orderid="+orders;
                    expiredTime = orders.getExpiresTime();
                    break;
            }

            String callback = Constants.payDomain+"/mobile/success?tradeid="+tradeid+"&tradetype="+tradetype;

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
            payOrder.setPayPlatform(platform);
            payOrder.setShowUrl(showUrl);
            payOrder.setCallbackUrl(callback);

            payOrder = financeService.createPayOrder(payOrder);
            if(payOrder == null || payOrder.getStatus() !=0)
            {
                return AjaxUtil.alert("支付订单异常。");
            }
            switch (platform)
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
                            payOrder.getDesc(),"", HttpUtils.getIp(request), payOrder.getExpiredTime());

                    payurl = "/mobile/pay?orderid="+payOrder.getOrderId()+"&payurl="+payurl;
            }
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return AjaxUtil.alert("访问出错！由于系统或网络造成的原因，请稍候再试。");
        }

        return AjaxUtil.goUrl(payurl,"");
    }

    /***
     * 支付提交成功，需触发本页相关js，再跳转至微信支付或银联支付
     * */
    @RequestMapping(value = "/pay", method = RequestMethod.GET)
    public String pay(String payurl,String orderid,Model model) {
        payurl = ConvertUtils.toString(payurl);
        orderid = ConvertUtils.toString(orderid);
        try {
            PayOrder payOrder = financeService.getPayOrder(orderid);
            if(payOrder !=null && payOrder.getStatus()==0) {
                model.addAttribute("payurl", payurl);
                model.addAttribute("platform", payOrder.getPayPlatform());
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }

        return "/mobile/pay";
    }
}

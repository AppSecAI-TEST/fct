package com.fct.web.pay.http.controller;

import com.fct.core.utils.AjaxUtil;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.HttpUtils;
import com.fct.finance.data.entity.PayOrder;
import com.fct.finance.data.entity.PayPlatform;
import com.fct.finance.data.entity.RechargeRecord;
import com.fct.finance.interfaces.FinanceService;
import com.fct.mall.data.entity.Orders;
import com.fct.mall.interfaces.MallService;
import com.fct.pay.interfaces.MobilePayService;
import com.fct.web.pay.http.cache.CacheManager;
import com.fct.web.pay.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "mobile")
public class MobileController extends BaseController{

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MobilePayService mobilePayService;

    @Autowired
    private MallService mallService;

    @Autowired
    private CacheManager cacheManager;

    /***
     *
     * 支付选择
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(HttpServletRequest request,String tradeid, String tradetype, Model model)
    {
        tradeid = ConvertUtils.toString(tradeid);
        tradetype =ConvertUtils.toString(tradetype);
        BigDecimal payAmount = new BigDecimal(0);
        Integer payStatus = 0;
        Integer memberId=0;

        PayOrder payOrder = null;
        String callback = String.format("%s/mobile/success?tradeid=%s&tradetype=%s",fctConfig.getPayUrl(),
                tradeid,tradetype);

        try {
            switch (tradetype) {
                case "buy":
                    Orders orders = cacheManager.getCacheOrders(tradeid);
                    payStatus = orders.getStatus();
                    payAmount = orders.getCashAmount();
                    memberId = orders.getMemberId();
                    //不需要用现金支付
                    if(payAmount.doubleValue()==0)
                    {
                        //保存支付方式（账户支付）
                        mallService.updateOrderPayPlatform(orders.getOrderId(),"account");

                        String desc="购买"+orders.getOrderGoods().get(0).getName();
                        if(orders.getOrderGoods().size()>1)
                        {
                            desc += "等多件";
                        }

                        String showUrl = fctConfig.getUrl()+"/my/orders/detail/"+orders.getOrderId();

                        payOrder = createPay(tradeid,tradetype,orders.getAccountAmount(),payAmount,orders.getTotalAmount(),
                                new BigDecimal(0),orders.getPoints(),
                                orders.getExpiresTime(),"account",showUrl,callback,desc);

                        if(payOrder == null || payOrder.getStatus() !=1)
                        {
                            return AjaxUtil.alert("支付订单异常。");
                        }
                    }

                    break;
                case "recharge":
                    RechargeRecord rechargeRecord = cacheManager.getCacheRechargeRecord(Integer.valueOf(tradeid));
                    payStatus = rechargeRecord.getStatus();
                    memberId = rechargeRecord.getMemberId();
                    payAmount = rechargeRecord.getPayAmount();

                    break;
                default:
                    return errorPage("非法用户操作");
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            return errorPage("系统或网络异常");
        }

        if(payStatus >1)
        {
            return errorPage("支付业务订单状态异常");
        }
        else if(payStatus ==1 || (payOrder !=null && payOrder.getStatus()==1))
        {
            return "redirect:"+callback;
        }
        if (!memberId.equals(currentUser.getMemberId())) {
            //出错跳转
            return errorPage("非法用户操作");
        }

        String userAgent = request.getHeader("user-agent").toLowerCase();
        if(userAgent.indexOf("micromessenger")>-1){//微信客户端
            model.addAttribute("weixin",1);
        }

        List<PayPlatform> platformList = financeService.findPayPlatform("wap","fangcun",1);
        model.addAttribute("platformList", platformList);
        model.addAttribute("length",platformList.size());
        model.addAttribute("payamount", payAmount);
        model.addAttribute("tradeid", tradeid);
        model.addAttribute("tradetype", tradetype);

        return "mobile/index";
    }

    private PayOrder createPay(String tradeid,String tradetype,BigDecimal accountAmount,BigDecimal payAmount,
                               BigDecimal totalAmount,BigDecimal discountAmount,Integer points,Date expiredTime,
                               String platform,String showUrl,String callback,String desc)
    {
        PayOrder payOrder = new PayOrder();
        payOrder.setCellPhone(currentUser.getCellPhone());
        payOrder.setMemberId(currentUser.getMemberId());
        payOrder.setTradeId(tradeid);
        payOrder.setTradeType(tradetype);
        payOrder.setAccountAmount(accountAmount);
        payOrder.setPayAmount(payAmount);
        payOrder.setTotalAmount(totalAmount);
        payOrder.setDiscountAmount(discountAmount);
        payOrder.setPoints(points);
        payOrder.setExpiredTime(expiredTime);
        payOrder.setPayPlatform(platform);
        payOrder.setShowUrl(showUrl);
        payOrder.setCallbackUrl(callback);
        payOrder.setDescription(desc);

        return financeService.createPayOrder(payOrder);
    }

    /**
     * 支付保存
     * */
    @RequestMapping(value = "/savepay", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String savePay(HttpServletRequest request,String tradeid,
                          String tradetype, String platform) {

        tradeid = ConvertUtils.toString(tradeid);
        tradetype =ConvertUtils.toString(tradetype);
        platform = ConvertUtils.toString(platform);

        if(StringUtils.isEmpty(tradeid) || StringUtils.isEmpty(tradetype) ||
                StringUtils.isEmpty(platform))
        {
            return AjaxUtil.alert("非法提交。");
        }

        if(platform.equals("wxpay_fctwap") && StringUtils.isEmpty(currentUser.getOpenId()))
        {
            //先清除用户登陆缓存数据，在跳转至授权页面。
            cacheManager.removeCacheMemberLogin(request);

            return AjaxUtil.goUrl(fctConfig.getUrl()+"/oauth","");
        }
        PayOrder payOrder = null;
        try
        {
            BigDecimal accountAmount =new BigDecimal(0);
            BigDecimal payAmount = new BigDecimal(0);
            BigDecimal totalAmount = new BigDecimal(0);
            BigDecimal discountAmount = new BigDecimal(0);
            Integer points =0;
            String desc="";
            Date expiredTime = null;
            String showUrl="";
            switch (tradetype)
            {
                case "buy":
                    Orders orders = mallService.getOrders(tradeid);
                    if(orders == null || orders.getStatus() !=0)
                    {
                        return AjaxUtil.alert("支付业务订单状态异常。");
                    }
                    if(!orders.getMemberId().equals(currentUser.getMemberId())) {
                        return AjaxUtil.alert("非法用户操作。");
                    }
                    if(DateUtils.compareDate(orders.getExpiresTime(),new Date())<=0)
                    {
                        return AjaxUtil.alert("订单已过期。");
                    }
                    orders.setPayPlatform(platform);
                    //保存支付方式
                    mallService.updateOrderPayPlatform(orders.getOrderId(),platform);

                    accountAmount = orders.getAccountAmount();
                    payAmount = orders.getCashAmount(); //现金应支付
                    totalAmount = orders.getTotalAmount();
                    points = orders.getPoints();
                    desc="购买"+orders.getOrderGoods().get(0).getName();
                    if(orders.getOrderGoods().size()>1)
                    {
                        desc += "等多件";
                    }
                    showUrl = fctConfig.getUrl()+"/my/orders/detail/"+orders.getOrderId();
                    //为业务特殊需求（延期过期时间）特将支付系统的过期时间再追加1天
                    expiredTime = DateUtils.addDay(orders.getExpiresTime(),1);
                    break;
                case "recharge":
                    RechargeRecord record = financeService.getRechargeRecord(Integer.valueOf(tradeid));
                    if(record == null || record.getStatus() !=0)
                    {
                        return AjaxUtil.alert("支付业务订单状态异常。");
                    }
                    if(!record.getMemberId().equals(currentUser.getMemberId())) {
                        return AjaxUtil.alert("非法用户操作。");
                    }
                    if(DateUtils.compareDate(record.getExpiredTime(),new Date())<=0)
                    {
                        return AjaxUtil.alert("订单已过期。");
                    }
                    record.setPayPlatform(platform);
                    //保存支付方式
                    financeService.updateRechargePayPlatform(record.getId(),platform);

                    payAmount = record.getPayAmount();
                    totalAmount = payAmount;
                    desc="在线充值";
                    showUrl = fctConfig.getUrl()+"/account";
                    expiredTime = record.getExpiredTime();
                    break;
            }

            String callback = String.format("%s/mobile/success?tradeid=%s&tradetype=%s",fctConfig.getPayUrl(),
                    tradeid,tradetype);

            payOrder = createPay(tradeid,tradetype,accountAmount,payAmount,totalAmount,discountAmount,points,
                    expiredTime,platform,showUrl,callback,desc);
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

        if(payOrder == null)
        {
            return AjaxUtil.alert("支付出错啦，请稍候再试！");
        }

        String payurl = "/mobile/pay?orderid=" + payOrder.getOrderId();

        return AjaxUtil.goUrl(payurl,"");
    }

    /***
     * 支付提交成功，需触发本页相关js，再跳转至微信支付或银联支付
     * */
    @RequestMapping(value = "/pay", method = RequestMethod.GET)
    public String pay(HttpServletRequest request,String orderid,Model model) {
        orderid = ConvertUtils.toString(orderid);

        if(StringUtils.isEmpty(orderid) || StringUtils.isEmpty(orderid))
        {
            return errorPage("支付参数错误，非法请求。");
        }
        String payurl = "";
        String platform = "";
        try {
            PayOrder payOrder = financeService.getPayOrder(orderid);

            if(payOrder == null || payOrder.getStatus() !=0)
            {
                return errorPage("支付订单异常。");
            }
            platform = payOrder.getPayPlatform();
            Date expiredTime = payOrder.getExpiredTime();
            switch (payOrder.getPayPlatform())
            {
                case "alipay_fctwap":
                    payurl = mobilePayService.alipayWap(platform, payOrder.getOrderId(), payOrder.getPayAmount(),
                            payOrder.getDescription(), expiredTime,"");
                    break;
                case "unionpay_fctwap":
                    payurl = mobilePayService.unionpayWap(platform,payOrder.getOrderId(),payOrder.getPayAmount(),
                            payOrder.getDescription(),expiredTime,"","");
                    break;

                case "wxpay_fctwap":
                    payurl =  mobilePayService.wxpayWap(platform,payOrder.getOrderId(),currentUser.getOpenId(),payOrder.getPayAmount(),
                            payOrder.getDescription(),"", HttpUtils.getIp(request), payOrder.getExpiredTime());
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        model.addAttribute("payurl", payurl);
        model.addAttribute("wxbackurl",fctConfig.getPayUrl() +"/mobile/wxpay/callback?orderid="+orderid);
        model.addAttribute("platform",platform);

        return "/mobile/pay";
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String success(String tradeid,String tradetype,Model model) {
        tradeid = ConvertUtils.toString(tradeid);
        tradetype = ConvertUtils.toString(tradetype);

        if(StringUtils.isEmpty(tradeid) || StringUtils.isEmpty(tradetype))
        {
            return errorPage("支付参数错误，非法请求。");
        }
        String remark = "";
        String gourl ="";
        try {
            PayOrder payOrder = financeService.getPayOrderByTrade(tradetype,tradeid);
            if(payOrder == null)
            {
                return errorPage("支付参数错误，非法请求。");
            }
            if(payOrder.getStatus() ==3){
                return errorPage("支付余额不足本次交易，所发生的现金系统将会在3个工作日内返回。");
            }
            if(payOrder.getStatus() != 1) {
                return errorPage("支付过程中发生错误：code:"+payOrder.getStatus());
            }
            switch (payOrder.getTradeType())
            {
                case "buy":
                    Orders orders = cacheManager.getCacheOrders(tradeid);
                    remark=orders.getOrderGoods().get(0).getName();
                    if(orders.getOrderGoods().size()>1)
                    {
                        remark += "等多件";
                    }
                    remark = "您购买的”"+remark +"”正在处理中，请耐心等待并关注订单状态。";
                    gourl = fctConfig.getUrl() +"/my/orders/detail/"+payOrder.getOrderId();
                    break;
                case "recharge":
                    RechargeRecord rechargeRecord = cacheManager.getCacheRechargeRecord(Integer.valueOf(payOrder.getTradeId()));
                    remark = String.format("您已成功充值%d元（包含赠送金额%d元）",
                            rechargeRecord.getAmount(),rechargeRecord.getGiftAmount());
                    gourl = fctConfig.getUrl() +"/my/account";
                    break;
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        if(StringUtils.isEmpty(remark))
        {
            return errorPage("支付参数错误，非法请求。");
        }

        model.addAttribute("orderid",tradeid);
        model.addAttribute("remark",remark);
        model.addAttribute("gourl",gourl);

        return "/mobile/success";
    }
}

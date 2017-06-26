package com.fct.web.pay.http.controller;

import com.fct.finance.data.entity.PayOrder;
import com.fct.finance.interfaces.FinanceService;
import com.fct.pay.interfaces.MobilePayService;
import com.fct.web.pay.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;

@RequestMapping(value = "mobile")
public class MobileController {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MobilePayService mobilePayService;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String index(String successurl, String showurl, String tradeid, String tradetype, String platform,
                             Integer memberid, String cellphone, BigDecimal accountamount, BigDecimal payamount,
                             Integer points, String desc, Integer expiretime, String openid, Model model) {

        //VerifySign();

        String payurl = "";

        try
        {
            PayOrder payOrder = new PayOrder();

            payOrder = financeService.createPayOrder(payOrder);
            if(payOrder != null)
            {
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

                       payurl =  mobilePayService.wxpayWap(payOrder.getOrderId(),openid,payamount,desc,"","userip",expiretime);
                }
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }

        if (platform.equals("alipay_fctwap"))
        {
            return "redirect:"+payurl;
        }

        if(StringUtils.isEmpty(payurl))
        {
            //输出错误
        }

        model.addAttribute("platform",l);
        model.addAttribute("payurl","");
        return "/mobile/index";
    }
}

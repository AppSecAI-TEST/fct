package com.fct.web.pay.http.controller.mobile;

import com.fct.core.json.JsonConverter;
import com.fct.finance.data.entity.PayOrder;
import com.fct.finance.interfaces.FinanceService;
import com.fct.pay.interfaces.MobilePayService;
import com.fct.pay.interfaces.PayNotify;
import com.fct.web.pay.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

@Controller("mobileAlipay")
@RequestMapping(value = "mobile/alipay")
public class AlipayController {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private MobilePayService mobilePayService;

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    @ResponseBody
    public void notify(HttpServletRequest request, HttpServletResponse response) {

        String successUrl = "";
        try
        {
            if (request.getInputStream() != null)
            {
                BufferedReader br=new BufferedReader(new InputStreamReader(request.getInputStream(),"utf-8"));

                String requestXml = br.readLine();

                Constants.logger.info("alipayMobileNotifyXML:" + requestXml);

                PayNotify payNotify = mobilePayService.unionpayNotify(Constants.getRequestData(requestXml,true));

                if (payNotify != null && !payNotify.getHasError())
                {
                    PayOrder payOrder = financeService.paySuccess(payNotify.getPayOrderNo(),
                            payNotify.getPayPlatform(), JsonConverter.toJson(payNotify.getExtandProperties()));
                    if(payOrder != null)
                    {
                        successUrl = payOrder.getCallbackUrl();
                    }
                }
                else
                {
                    Constants.logger.warn(JsonConverter.toJson(payNotify));
                }
            }

            PrintWriter out = response.getWriter();
            if(!StringUtils.isEmpty(successUrl)) {
                out.println("success");
            }else
            {
                out.println("fail");
            }
            out.flush();
            out.close();

        }
        catch (Exception exp)
        {
            Constants.logger.error("异常数据处理银联支付过程中发生错误。" + exp);
        }
    }

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public String callback(HttpServletRequest request,HttpServletResponse response) {

        String successUrl = "";
        try
        {
            Map<String, String> dicParam = Constants.getAllRequestParam(request,true);

            PayNotify payNotify = mobilePayService.unionpayCallBack(dicParam);

            if (payNotify != null && !payNotify.getHasError())
            {
                PayOrder payOrder = financeService.paySuccess(payNotify.getPayOrderNo(),
                        payNotify.getPayPlatform(), JsonConverter.toJson(payNotify.getExtandProperties()));
                if(payOrder != null)
                {
                    successUrl = payOrder.getCallbackUrl();
                }
            }
            else
            {
                Constants.logger.warn(JsonConverter.toJson(payNotify));
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error("异常数据处理银联支付过程中发生错误。" + exp);
        }

        if(!StringUtils.isEmpty(successUrl))
        {
            return "redirect:"+successUrl;
        }

        try {
            PrintWriter out = response.getWriter();
            out.println("fail");
            out.flush();
            out.close();
        }
        catch (IOException exp)
        {
            Constants.logger.error("支付宝支付完成页面处理过程中发生错误。" + exp);
        }

        return "mobile/alipay/callback";
    }
}

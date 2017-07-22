package com.fct.pay.service.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StringUtils;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.DateUtils;
import com.fct.pay.interfaces.PayNotify;
import com.fct.pay.service.PayConfig;
import com.fct.pay.service.unionpay.LogUtil;
import com.fct.pay.service.unionpay.SDKUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by jon on 2017/7/21.
 */

@Service
public class AlipayManager {

    @Autowired
    private PayConfig payConfig;

    public void initSDKConfiguration(String payment){

        SDKConfig.setCHARSET("UTF-8");
        SDKConfig.setFORMAT("json");
        SDKConfig.setSIGNTYPE("RSA2");
        SDKConfig.setURL("https://openapi.alipaydev.com/gateway.do");

        Map<String, String> config = payConfig.getAlipay_fct();

        SDKConfig.setAPPID(config.get("appid"));
        SDKConfig.setRsaPrivateKey(txt2String(config.get("privatekey_path")));
        SDKConfig.setAlipayPublicKey(txt2String(config.get("publickey_path")));
        SDKConfig.setNotify_url(config.get("notifyurl"));
        SDKConfig.setApp_notify_url(config.get("appnotifyurl"));
        SDKConfig.setReturn_url(config.get("returnurl"));

    }

    public String createWapPayUrl(String payment,String out_trade_no, String subject, BigDecimal total_amount,
                                  Date expireTime)
    {
        initSDKConfiguration(payment);

        if(StringUtils.isEmpty(out_trade_no))
        {
            throw new IllegalArgumentException("交易号为空");
        }

        if(StringUtils.isEmpty(subject))
        {
            throw new IllegalArgumentException("交易标题为空");
        }

        if(total_amount.doubleValue()<=0)
        {
            throw new IllegalArgumentException("交易金额非法");
        }

        if(DateUtils.compareDate(expireTime,new Date())<=0)
        {
            throw new IllegalArgumentException("过期时间非法");
        }

        // 超时时间 可空
        String timeExpire = DateUtils.formatDate(expireTime,"yyyy-MM-dd HH:mm");
        // 销售产品码 必填
        String product_code="QUICK_WAP_PAY";
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        //调用RSA签名方式
        AlipayClient client = new DefaultAlipayClient(SDKConfig.getURL(),SDKConfig.getAPPID(),SDKConfig.getRsaPrivateKey(),
                SDKConfig.getFORMAT(), SDKConfig.getCHARSET(), SDKConfig.getAlipayPublicKey(),SDKConfig.getSIGNTYPE());

        AlipayTradeAppPayRequest alipay_request=new AlipayTradeAppPayRequest();

        // 封装请求支付信息
        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
        model.setOutTradeNo(out_trade_no);
        model.setSubject(subject);
        model.setTotalAmount(total_amount.toString());
        model.setBody("");
        model.setTimeExpire(timeExpire);
        model.setProductCode(product_code);
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(SDKConfig.getNotify_url());
        // 设置同步地址
        alipay_request.setReturnUrl(SDKConfig.getReturn_url());

        // form表单生产
        String payUrl = "";
        try {
            // 调用SDK生成表单
            payUrl = client.pageExecute(alipay_request).getBody();

        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogUtil.writeMessage("支付宝支付发起的请求地址:" + JsonConverter.toJson(payUrl));
        return payUrl;
    }

    /**
     * 读取txt文件的内容
     * @param path 想要读取的文件对象
     * @return 返回文件内容
     */
    private static String txt2String(String path){

        StringBuilder result = new StringBuilder();
        try{

            File file = new File(path);

            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }

    public PayNotify notify(Map<String, String> params)
    {
        PayNotify notify = new PayNotify();

        if (params == null || params.size() <= 0)
        {
            notify.setErrorMessage("请求过来的参数对列为空");
            notify.setHasError(true);
            return notify;
        }

        String payment = "alipay_fct";
        initSDKConfiguration(payment);

        LogUtil.writeMessage("支付宝无线支付传输过来的参数值:" + JsonConverter.toJson(params));

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        //商户订单号

        String out_trade_no = params.get("out_trade_no");
        //支付宝交易号

        String trade_no = params.get("trade_no");

        //交易状态
        String trade_status = params.get("trade_status");

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)

        try {
            boolean verify_result = AlipaySignature.rsaCheckV1(params,
                    SDKConfig.getAlipayPublicKey(), SDKConfig.getCHARSET(), "RSA2");

            if(verify_result){//验证成功
                //////////////////////////////////////////////////////////////////////////////////////////
                //请在这里加上商户的业务逻辑程序代码

                //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

                if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
                    //如果有做过处理，不执行商户的业务程序

                    //注意：
                    //如果签约的是可退款协议，退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                    //如果没有签约可退款协议，那么付款完成后，支付宝系统发送该交易状态通知。
                    notify.setHasError(false);
                    notify.setPayOrderNo(out_trade_no);
                    notify.setExtandProperties(params);
                    notify.setPayPlatform(payment);

                    LogUtil.writeMessage("支付订单（" + notify.getPayOrderNo() + "）处理成功。");
                }

                //////////////////////////////////////////////////////////////////////////////////////////
            }else{//验证失败
                notify.setHasError(true);
                notify.setErrorMessage("支付宝无线支付平台支付签名验证失败。");
            }

        }
        catch (AlipayApiException e)
        {
            e.printStackTrace();
        }
        return  notify;

    }

    public PayNotify callback(Map<String, String> params)
    {
        PayNotify notify = new PayNotify();

        if (params == null || params.size() <= 0)
        {
            notify.setErrorMessage("请求过来的参数对列为空");
            notify.setHasError(true);
            return notify;
        }

        String payment = "alipay_fct";
        initSDKConfiguration(payment);

        LogUtil.writeMessage("支付宝无线支付传输过来的参数值:" + JsonConverter.toJson(params));

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        //商户订单号

        String out_trade_no = params.get("out_trade_no");
        //支付宝交易号

        String trade_no = params.get("trade_no");

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        //计算得出通知验证结果
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)

        try {
            boolean verify_result = AlipaySignature.rsaCheckV1(params,
                    SDKConfig.getAlipayPublicKey(), SDKConfig.getCHARSET(), "RSA2");

            if(verify_result){//验证成功
                //////////////////////////////////////////////////////////////////////////////////////////
                //请在这里加上商户的业务逻辑程序代码

                //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——

                notify.setHasError(false);
                notify.setPayOrderNo(out_trade_no);
                notify.setExtandProperties(params);
                notify.setPayPlatform(payment);

                //////////////////////////////////////////////////////////////////////////////////////////
            }else{//验证失败
                notify.setHasError(true);
                notify.setErrorMessage("支付宝无线支付平台支付签名验证失败。");
            }

        }
        catch (AlipayApiException e)
        {
            e.printStackTrace();
        }
        return  notify;

    }

    public PayNotify refund(String payment,String out_trade_no,String out_request_no,BigDecimal refundAmount,
                            String refund_reason)
    {
        if(StringUtils.isEmpty(out_trade_no))
        {
            throw new IllegalArgumentException("支付交易号为空");
        }

        if(StringUtils.isEmpty(out_request_no))
        {
            throw new IllegalArgumentException("退款交易号为空");
        }

        if(refundAmount.doubleValue()<=0)
        {
            throw new IllegalArgumentException("退款金额非法");
        }

        if(StringUtils.isEmpty(refund_reason))
        {
            throw new IllegalArgumentException("退款理由为空");
        }

        initSDKConfiguration(payment);

        //商户订单号和支付宝交易号不能同时为空。 trade_no、  out_trade_no如果同时存在优先取trade_no
        //退款金额，不能大于订单总金额
        String refund_amount= refundAmount.toString();
        //退款的原因说明
        //标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        AlipayClient client = new DefaultAlipayClient(SDKConfig.getURL(),SDKConfig.getAPPID(),SDKConfig.getRsaPrivateKey(),
                SDKConfig.getFORMAT(), SDKConfig.getCHARSET(), SDKConfig.getAlipayPublicKey(),SDKConfig.getSIGNTYPE());

        AlipayTradeRefundRequest alipay_request = new AlipayTradeRefundRequest();

        AlipayTradeRefundModel model=new AlipayTradeRefundModel();
        model.setOutTradeNo(out_trade_no);
        model.setTradeNo("");
        model.setRefundAmount(refund_amount);
        model.setRefundReason(refund_reason);
        model.setOutRequestNo(out_request_no);
        alipay_request.setBizModel(model);

        try {
            AlipayTradeRefundResponse alipay_response = client.execute(alipay_request);

            alipay_response.getBody();

            LogUtil.writeMessage("支付宝退款处理结果:" + alipay_response.getBody());
        }
        catch (AlipayApiException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}

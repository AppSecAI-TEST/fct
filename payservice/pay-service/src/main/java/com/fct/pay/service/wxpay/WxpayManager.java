package com.fct.pay.service.wxpay;

import com.alibaba.dubbo.common.json.JSONConverter;
import com.fct.core.json.JsonConverter;
import com.fct.core.utils.DateUtils;
import com.fct.pay.interfaces.PayNotify;
import com.fct.pay.service.Constants;
import com.fct.pay.service.PayConfig;
import com.fct.pay.service.wxpay.business.DownloadBillBusiness;
import com.fct.pay.service.wxpay.business.RefundBusiness;
import com.fct.pay.service.wxpay.business.RefundQueryBusiness;
import com.fct.pay.service.wxpay.business.ScanPayBusiness;
import com.fct.pay.service.wxpay.common.*;
import com.fct.pay.service.wxpay.protocol.downloadbill_protocol.DownloadBillReqData;
import com.fct.pay.service.wxpay.protocol.pay_protocol.ScanPayReqData;
import com.fct.pay.service.wxpay.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.fct.pay.service.wxpay.protocol.refund_protocol.RefundReqData;
import com.fct.pay.service.wxpay.protocol.refund_query_protocol.RefundQueryReqData;
import com.fct.pay.service.wxpay.protocol.reverse_protocol.ReverseReqData;
import com.fct.pay.service.wxpay.protocol.unifiedorder.UnifiedOrderReqData;
import com.fct.pay.service.wxpay.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * SDK总入口
 */
@Service
public class WxpayManager {

    @Autowired
    private PayConfig payConfig;

    /**
     * 初始化SDK依赖的几个关键配置
     * @param key 签名算法需要用到的秘钥
     * @param appID 公众账号ID
     * @param mchID 商户ID
     * @param sdbMchID 子商户ID，受理模式必填
     * @param certLocalPath HTTP证书在服务器中的路径，用来加载证书用
     * @param certPassword HTTP证书的密码，默认等于MCHID
     */
    public void initSDKConfiguration(String key,String appID,String mchID,String sdbMchID,String certLocalPath,String certPassword){
        Configure.setKey(key);
        Configure.setAppID(appID);
        Configure.setMchID(mchID);
        Configure.setSubMchID(sdbMchID);
        Configure.setCertLocalPath(certLocalPath);
        Configure.setCertPassword(certPassword);
    }

    private void initSDKConfiguration(String payment,String userip){

        Map<String, String> config = null;
        switch (payment)
        {
            case "wxpay_fctapp":
                config = payConfig.getWxpay_fctapp();
                break;
            default:
                config = payConfig.getWxpay_fctwap();
                break;
        }

        Configure.setKey(config.get("key"));
        Configure.setAppID(config.get("appid"));
        Configure.setMchID(config.get("mchid"));
        Configure.setSubMchID("");
        Configure.setCertLocalPath(config.get("cert_path")); //"cert\\wxpayapp\\1442883002_cert.p12";
        Configure.setCertPassword(config.get("cert_password"));
        Configure.setNotifyUrl(config.get("notifyurl"));
        Configure.setIp(userip);
    }

    /**
     * 请求支付服务
     * @param scanPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的数据
     * @throws Exception
     */
    public String requestScanPayService(ScanPayReqData scanPayReqData) throws Exception{
        return new ScanPayService().request(scanPayReqData);
    }

    public String requestUnifiedOrderService(String payment,String payOrderId, String openId, BigDecimal total_fee, String body,
                                                    String notifyUrl, String userIp,Date expireTime) {

        if (StringUtils.isEmpty(payment)) {
            throw new IllegalArgumentException("支付方式不空");
        }
        if (StringUtils.isEmpty(payOrderId))
        {
            throw new IllegalArgumentException("支付订单为空");
        }
        if(StringUtils.isEmpty(userIp))
        {
            throw new IllegalArgumentException("用户ip为空");
        }
        if(StringUtils.isEmpty(body))
        {
            throw new IllegalArgumentException("描述为空");
        }
        if(StringUtils.isEmpty(openId))
        {
            throw new IllegalArgumentException("openid为空");
        }
        if(total_fee.doubleValue()<=0)
        {
            throw new IllegalArgumentException("支付金额不正确。");
        }
        if(DateUtils.compareDate(expireTime,new Date())<=0)
        {
            throw new IllegalArgumentException("已过期不可进行支付。");
        }

        //Integer expirtime = expireMinutes > 0 ? expireMinutes : 7200; //以分为单位，默认5天
        initSDKConfiguration(payment,userIp);

        Integer totalFee =  total_fee.multiply(new BigDecimal(100)).intValue();
        String timeStart = DateUtils.formatDate(new Date(),"yyyyMMddHHmmss");

        String timeExpire = DateUtils.formatDate(expireTime,"yyyyMMddHHmmss");

        UnifiedOrderReqData unifiedOrderReqData = new UnifiedOrderReqData(openId,body,payOrderId,totalFee,userIp,
                timeStart,timeExpire,"JSAPI",Configure.getNotifyUrl());
        Map<String,Object> map = null;
        try {
            String reqdata = new UnifiedOrderService().request(unifiedOrderReqData);
            map = XMLParser.getMapFromXML(reqdata);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }

        if (map == null || !map.containsKey("appid") || !map.containsKey("prepay_id") || map.get("prepay_id").toString() == "")
        {
            Constants.logger.info("wxpay:UnifiedOrder response error!" + JsonConverter.toJson(map));
            throw new IllegalArgumentException("请求微信支付过程中出错，请稍候再试。");
        }

        Map<String,Object> jsAPI = new HashMap<String, Object>();

        jsAPI.put("appId",map.get("appid"));
        jsAPI.put("timeStamp", RandomStringGenerator.getGenerateTimeStamp());
        jsAPI.put("nonceStr", RandomStringGenerator.getRandomStringByLength(12));
        jsAPI.put("package","prepay_id=" + map.get("prepay_id"));
        jsAPI.put("signType",map.get("MD5"));
        jsAPI.put("paySign", Signature.getSign(jsAPI));

        String jsonParam = JsonConverter.toJson(jsAPI);

        Constants.logger.info("Get jsApiParam : " + jsonParam);

        return jsonParam;
    }

    public String requestAppPay(String payment, String payOrderId, BigDecimal total_fee, String body,
                                String notifyUrl, String userIp, Date expireTime) {
        if (StringUtils.isEmpty(payment)) {
            throw new IllegalArgumentException("支付方式不空");
        }
        if (StringUtils.isEmpty(payOrderId))
        {
            throw new IllegalArgumentException("支付订单为空");
        }
        if(StringUtils.isEmpty(userIp))
        {
            throw new IllegalArgumentException("用户ip为空");
        }
        if(StringUtils.isEmpty(body))
        {
            throw new IllegalArgumentException("描述为空");
        }
        if(total_fee.doubleValue()<=0)
        {
            throw new IllegalArgumentException("支付金额不正确。");
        }
        if(DateUtils.compareDate(expireTime,new Date())<=0)
        {
            throw new IllegalArgumentException("已过期不可进行支付。");
        }
        //统一下单
       initSDKConfiguration(payment,userIp);

        Integer totalFee =  total_fee.multiply(new BigDecimal(100)).intValue();
        String timeStart = DateUtils.formatDate(new Date(),"yyyyMMddHHmmss");

        String timeExpire = DateUtils.formatDate(expireTime,"yyyyMMddHHmmss");

        UnifiedOrderReqData unifiedOrderReqData = new UnifiedOrderReqData("",body,payOrderId,totalFee,userIp,
                timeStart,timeExpire,"APP",Configure.getNotifyUrl());

        Map<String,Object> map = null;
        try {
            String reqdata = new UnifiedOrderService().request(unifiedOrderReqData);
            map = XMLParser.getMapFromXML(reqdata);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }

        if (map == null || !map.containsKey("appid") || !map.containsKey("prepay_id") || map.get("prepay_id").toString() == "")
        {
            Constants.logger.info("wxpay:UnifiedOrder response error!");
            throw new IllegalArgumentException("unifiedOrder response error!");
        }

        Map<String,Object> jsAPI = new HashMap<String, Object>();

        jsAPI.put("appid",map.get("appid"));
        jsAPI.put("partnerid",Configure.getMchid());
        jsAPI.put("timestamp", RandomStringGenerator.getGenerateTimeStamp());
        jsAPI.put("nonceStr", RandomStringGenerator.getRandomStringByLength(12));
        jsAPI.put("prepayid",map.get("prepay_id"));
        jsAPI.put("package","Sign=WXPay");
        jsAPI.put("sign", Signature.getSign(jsAPI));

        String jsonParam = JsonConverter.toJson(jsAPI);

        Constants.logger.info("Get jsApiParam : " + jsonParam);

        return jsonParam;
    }

    /***
     * h5与app通用
     * */
    public PayNotify payNotify(Map<String, String> mapParam, String xmlContent)
    {
        PayNotify notify = new PayNotify();
        if (mapParam == null)
        {
            mapParam = new HashMap<>();
        }

        if (StringUtils.isEmpty(xmlContent))
        {
            throw new IllegalArgumentException("xmlContent is null");
        }

        try {

            Map<String, Object> map = XMLParser.getMapFromXML(xmlContent);

            String payment = getNotifyPayment(map);

            initSDKConfiguration(payment,"");

            if (!Signature.checkIsSignValidFromResponseMap(map)) {
                throw new IllegalArgumentException("WxPayData签名验证错误");
            }

            //检查支付结果中transaction_id是否存在
            if (!map.containsKey("transaction_id")) {
                map = new HashMap<>();
                map.put("return_code", "FAIL");
                map.put("return_msg", "支付结果中微信订单号不存在");

                notify.setErrorMessage(XMLParser.getXmltoMap(map));
                notify.setHasError(true);
                return notify;
            }

            String transaction_id = map.get("transaction_id").toString();

            //查询订单，判断订单真实性
            if (!QueryOrder(transaction_id,map.get("out_trade_no").toString())) {
                //若订单查询失败，则立即返回结果给微信支付后台
                map = new HashMap<>();
                map.put("return_code", "FAIL");
                map.put("return_msg", "订单查询失败");

                notify.setErrorMessage(XMLParser.getXmltoMap(map));
                notify.setHasError(true);

            }
            //查询订单成功
            else {

                notify.setPayOrderNo(map.get("out_trade_no").toString());
                notify.setHasError(false);
                //notify.setExtandProperties(XMLParser.getMapConvertToMap(map));
                notify.setPayPlatform(payment);

                map = new HashMap<>();
                map.put("return_code", "SUCCESS");
                map.put("return_msg", "订单查询失败");

                notify.setErrorMessage(XMLParser.getXmltoMap(map));

                Constants.logger.info("支付订单（" + notify.getPayOrderNo() + "）处理成功。");

            }
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }

        return notify;
    }

    //查询订单
    private  Boolean QueryOrder(String transaction_id,String out_tradeNo)
    {
        ScanPayQueryReqData scanPayQueryReqData = new ScanPayQueryReqData(transaction_id,out_tradeNo);

        try {
            String reqdata = new ScanPayQueryService().request(scanPayQueryReqData);

            Map<String, Object> map = XMLParser.getMapFromXML(reqdata);

            if (map.get("return_code").toString() == "SUCCESS" &&
                    map.get("result_code").toString() == "SUCCESS") {
                return true;
            }
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
        return false;
    }

    /**
     * 请求支付查询服务
     * @param scanPayQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public String requestScanPayQueryService(ScanPayQueryReqData scanPayQueryReqData) throws Exception{
		return new ScanPayQueryService().request(scanPayQueryReqData);
	}

    /**
     * 请求退款服务
     * @param payment 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
    public PayNotify requestRefundService(String payment,String payOrderId,String refundId,
                                                 BigDecimal payAmount,BigDecimal refundAmount){

        initSDKConfiguration(payment,"");

        RefundReqData refundReqData = new RefundReqData(payOrderId,payOrderId,"",refundId,
                payAmount.multiply(new BigDecimal(100)).intValue(),refundAmount.multiply(new BigDecimal(100)).intValue(),
                Configure.getMchid(),"");

        Map<String,Object> result = null;
        PayNotify notify = new PayNotify();

        try {
            String reqdata = new RefundService().request(refundReqData);//提交退款申请给API，接收返回数据
            result = XMLParser.getMapFromXML(reqdata);

            Constants.logger.info("wxpay:" + reqdata);

            if (result.get("return_code").toString().toLowerCase() == "success" &&
                    result.get("result_code").toString().toLowerCase() == "success" &&
                    result.get("out_trade_no").toString() == payOrderId
                    )
            {
                //交易成功并在页面返回success
                notify.setHasError(false);
                notify.setPayOrderNo(refundId);
                notify.setExtandProperties(XMLParser.getMapConvertToMap(result));
            }
            else
            {
                //签名验证失败
                notify.setHasError(true);
                notify.setErrorMessage("微信支付平台退款异步通知签名验证失败.");
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return notify;
    }

    /**
     * 请求退款查询服务
     * @param refundQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public String requestRefundQueryService(RefundQueryReqData refundQueryReqData) throws Exception{
		return new RefundQueryService().request(refundQueryReqData);
	}

    /**
     * 请求撤销服务
     * @param reverseReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public String requestReverseService(ReverseReqData reverseReqData) throws Exception{
		return new ReverseService().request(reverseReqData);
	}

    /**
     * 请求对账单下载服务
     * @param downloadBillReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
    public String requestDownloadBillService(DownloadBillReqData downloadBillReqData) throws Exception{
        return new DownloadBillService().request(downloadBillReqData);
    }

    /**
     * 直接执行被扫支付业务逻辑（包含最佳实践流程）
     * @param scanPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public void doScanPayBusiness(ScanPayReqData scanPayReqData, ScanPayBusiness.ResultListener resultListener) throws Exception {
        new ScanPayBusiness().run(scanPayReqData, resultListener);
    }

    /**
     * 调用退款业务逻辑
     * @param refundReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 业务逻辑可能走到的结果分支，需要商户处理
     * @throws Exception
     */
    public void doRefundBusiness(RefundReqData refundReqData, RefundBusiness.ResultListener resultListener) throws Exception {
        new RefundBusiness().run(refundReqData,resultListener);
    }

    /**
     * 运行退款查询的业务逻辑
     * @param refundQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public void doRefundQueryBusiness(RefundQueryReqData refundQueryReqData, RefundQueryBusiness.ResultListener resultListener) throws Exception {
        new RefundQueryBusiness().run(refundQueryReqData,resultListener);
    }

    /**
     * 请求对账单下载服务
     * @param downloadBillReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @return API返回的XML数据
     * @throws Exception
     */
    public void doDownloadBillBusiness(DownloadBillReqData downloadBillReqData,DownloadBillBusiness.ResultListener resultListener) throws Exception {
        new DownloadBillBusiness().run(downloadBillReqData,resultListener);
    }



    /// <summary>
    /// 获取异步通知支付平台方式
    /// </summary>
    /// <param name="dic"></param>
    /// <returns></returns>
    private String getNotifyPayment(Map<String,Object> dic)
    {
        String[] arrAppId = payConfig.getPlatform_ids().get("wxpay_appids").split("|");
        for (int i = 0; i < arrAppId.length; i++)
        {
            if (arrAppId[i].contains(dic.get("appid").toString()))
            {
                return arrAppId[i].split("#")[0];
            }
        }
        return "";
    }


}

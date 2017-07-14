package com.fct.pay.service.unionpay;

import com.fct.core.json.JsonConverter;
import com.fct.core.utils.DateUtils;
import com.fct.pay.interfaces.PayNotify;
import com.fct.pay.service.Constants;
import com.fct.pay.service.PayConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class UnionPayManager {

    @Autowired
    private PayConfig payConfig;

    public void initSDKConfiguration(String payment){
        SDKConfig.setVersion("5.0.0");//版本号
        SDKConfig.setEncoding("UTF-8");//编码方式
        SDKConfig.setBizType("000201");//业务类型
        SDKConfig.setSignMethod("01");//签名方法
        SDKConfig.setAccessType("0");//接入类型
        SDKConfig.setCurrencyCode("156");//交易币种

        Map<String, String> config = payConfig.getUnionpay_fctwap();
        SDKConfig.setMerId(config.get("meriId"));
        SDKConfig.setFrontTransUrl(config.get("frontTransUrl"));
        SDKConfig.setBackTransUrl(config.get("backTransUrl"));
        SDKConfig.setEncryptCertPath(config.get("encryptCert_path"));
        SDKConfig.setSignCertPath(Constants.getProjectPath()+config.get("signCert_path"));
        String signCertpwd = config.get("signCert_pwd");
        if(signCertpwd.equals("0") || signCertpwd.length()<5)
            signCertpwd = "000000";

        SDKConfig.setSignCertPwd(signCertpwd);

        SDKConfig.setValidateCertDir(Constants.getProjectPath()+config.get("validateCert_dir"));
        SDKConfig.setBackUrl(config.get("backUrl"));
        SDKConfig.setFrontUrl(config.get("frontUrl"));
        SDKConfig.setRefundBackUrl(config.get("refund_backUrl"));
        SDKConfig.setSingleMode("true");
        SDKConfig.setSignCertType("PKCS12");    //签名证书类型，固定不需要修改
    }

    public String createWapPayUrl(String payment,String orderId, BigDecimal payAmount, String desc, Date expireTime,
                                  String notifyUrl, String callbackUrl)
    {
        /**
         * 重要：联调测试时请仔细阅读注释！
         *
         * 产品：跳转网关支付产品<br>
         * 交易：消费：前台跳转，有前台通知应答和后台通知应答<br>
         * 日期： 2015-09<br>
         * 版本： 1.0.0
         * 版权： 中国银联<br>
         * 说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码性能规范性等方面的保障<br>
         * 提示：该接口参考文档位置：open.unionpay.com帮助中心 下载  产品接口规范  《网关支付产品接口规范》，<br>
         *              《平台接入接口规范-第5部分-附录》（内包含应答码接口规范，全渠道平台银行名称-简码对照表)<br>
         *              《全渠道平台接入接口规范 第3部分 文件接口》（对账文件格式说明）<br>
         * 测试过程中的如果遇到疑问或问题您可以：1）优先在open平台中查找答案：
         * 							        调试过程中的问题或其他问题请在 https://open.unionpay.com/ajweb/help/faq/list 帮助中心 FAQ 搜索解决方案
         *                             测试过程中产生的6位应答码问题疑问请在https://open.unionpay.com/ajweb/help/respCode/respCodeList 输入应答码搜索解决方案
         *                          2） 咨询在线人工支持： open.unionpay.com注册一个用户并登陆在右上角点击“在线客服”，咨询人工QQ测试支持。
         * 交易说明:1）以后台通知或交易状态查询交易确定交易成功,前台通知不能作为判断成功的标准.
         *       2）交易状态查询交易（Form_6_5_Query）建议调用机制：前台类交易建议间隔（5分、10分、30分、60分、120分）发起交易查询，如果查询到结果成功，则不用再查询。（失败，处理中，查询不到订单均可能为中间状态）。也可以建议商户使用payTimeout（支付超时时间），过了这个时间点查询，得到的结果为最终结果。
         */
        initSDKConfiguration(payment);

        String timeStart = DateUtils.formatDate(new Date(),"yyyyMMddHHmmss");

        //String timeExpire = DateUtils.formatDate(expireTime,"yyyyMMddHHmmss");

        Integer totalFee =  payAmount.multiply(new BigDecimal(100)).intValue();

        Map<String, String> param = new HashMap<String, String>();
        param.put("version",SDKConfig.getVersion());
        param.put("encoding",SDKConfig.getEncoding());
        //param.put("certId",CertUtil.getSignCertId()); //证书ID

        param.put("txnType","01");  //交易类型
        param.put("txnSubType","01");   //交易子类
        param.put("bizType",SDKConfig.getBizType());
        param.put("signMethod",SDKConfig.getSignMethod());
        param.put("channelType","07");//渠道类型，这个字段区分B2C网关支付和手机wap支付；07：PC,平板  08：手机
        param.put("accessType",SDKConfig.getAccessType());
        param.put("frontUrl", StringUtils.isEmpty(callbackUrl) ?  SDKConfig.getFrontUrl() : callbackUrl);//前台通知地址
        param.put("backUrl",StringUtils.isEmpty(notifyUrl) ? SDKConfig.getBackUrl() : notifyUrl);   //后台通知地址

        param.put("currencyCode",SDKConfig.getCurrencyCode());
        param.put("merId",SDKConfig.getMerId());    //商户号，请改自己的测试商户号，此处默认取demo演示页面传递的参数
        param.put("orderId",orderId);   //商户订单号，8-32位数字字母，不能含“-”或“_”，此处默认取demo演示页面传递的参数，可以自行定制规则
        param.put("txnTime",timeStart);//订单发送时间，格式为YYYYMMDDhhmmss，取北京时间，此处默认取demo演示页面传递的参数，参考取法： DateTime.Now.ToString("yyyyMMddHHmmss")
        param.put("txnAmt",totalFee.toString()); //交易金额，单位分，此处默认取demo演示页面传递的参数

        //TODO 其他特殊用法请查看 pages/api_01_gateway/special_use_purchase.htm

        /**请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面**/
        Map<String, String> submitFromData = AcpService.sign(param,SDKConfig.getEncoding());  //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

        //获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.frontTransUrl
        /*String html = AcpService.createAutoFormHtml(SDKConfig.getFrontUrl(), submitFromData,SDKConfig.getEncoding());   //生成自动跳转的Html表单

        LogUtil.writeLog("打印请求HTML，此为请求报文，为联调排查问题的依据："+html);*/
        //将生成的html写到浏览器中完成自动跳转打开银联支付页面；这里调用signData之后，将html写到浏览器跳转到银联页面之前均不能对html中的表单项的名称和值进行修改，如果修改会导致验签不通过
        //SDKUtil.sign(param,SDKConfig.getEncoding());

        String url = SDKConfig.getFrontTransUrl() + SDKUtil.createLinkString(submitFromData, false,true);

        LogUtil.writeMessage("unionpay_mobile_url:" + url);
        return url;
    }

    /// <summary>
    /// 同步验证处理银联无线支付平台
    /// </summary>
    /// <returns></returns>
    public PayNotify callBack(Map<String, String> map)
    {
        PayNotify notify = new PayNotify();

        if (map == null || map.size() <= 0)
        {
            notify.setErrorMessage("请求过来的参数对列为空");
            notify.setHasError(true);
            return notify;
        }

        //map = getSortMap(map);

        String unionPaySign = map.get("signature");

        String payment = "unionpay_fctwap";
        initSDKConfiguration(payment);

        LogUtil.writeMessage("银联无线支付传输过来的参数值:" + JsonConverter.toJson(map));

        // 返回报文中不包含UPOG,表示Server端正确接收交易请求,则需要验证Server端返回报文的签名
        if (AcpService.validate(map, SDKConfig.getEncoding()))
        {
            //Response.Write("商户端验证返回报文签名成功\n");
            String respcode = map.get("respCode"); //00、A6为成功，其余为失败。其他字段也可按此方式获取。
            //交易成功，请填写自己的业务代码
            notify.setHasError(false);
            notify.setPayOrderNo(map.get("orderId"));
            notify.setExtandProperties(map);
            notify.setPayPlatform(payment);

            LogUtil.writeMessage("支付订单（" + notify.getPayOrderNo() + "）处理成功。");
        }
        else
        {
            notify.setHasError(true);
            notify.setErrorMessage("银联无线支付平台支付签名验证失败，验证结果为： "+unionPaySign);
        }

        return notify;
    }

    /// <summary>
    /// 异步验证银联无线平台支付返回签名
    /// </summary>
    /// <returns></returns>
    public PayNotify notify(Map<String, String> map)
    {
        PayNotify notify = new PayNotify();
        if (map == null || map.size() <= 0)
        {
            notify.setErrorMessage("请求过来的参数对列为空");
            notify.setHasError(true);
            return notify;
        }
        String payment = "unionpay_fctwap";
        initSDKConfiguration(payment);

        String unionPaySign = map.get("signature");

        LogUtil.writeMessage("银联无线支付传输过来的参数值:" + JsonConverter.toJson(map));

        //map = getSortMap(map);

        // 返回报文中不包含UPOG,表示Server端正确接收交易请求,则需要验证Server端返回报文的签名
        if (AcpService.validate(map, SDKConfig.getEncoding()))
        {
            //Response.Write("商户端验证返回报文签名成功\n");
            String respcode = map.get("respCode"); //00、A6为成功，其余为失败。其他字段也可按此方式获取。
            if(respcode =="00") {
                //交易成功，请填写自己的业务代码
                notify.setHasError(false);
                notify.setPayOrderNo(map.get("orderId"));
                notify.setExtandProperties(map);
                notify.setPayPlatform(payment);

                LogUtil.writeMessage("支付订单（" + notify.getPayOrderNo() + "）处理成功。");
            }
            else
            {
                notify.setHasError(true);
                notify.setErrorMessage("银联无线支付平台支付失败： "+respcode);
            }
        }
        else
        {
            notify.setHasError(true);
            notify.setErrorMessage("银联无线支付平台支付签名验证失败，验证结果为： "+unionPaySign);
        }

        return notify;
    }

    public PayNotify refund(String payment,String refundId, String queryId, BigDecimal totalAmount, BigDecimal refundAmount)
    {
        /**
         * 重要：联调测试时请仔细阅读注释！
         *
         * 产品：跳转网关支付产品<br>
         * 交易：退货交易：后台资金类交易，有同步应答和后台通知应答<br>
         * 日期： 2015-09<br>
         * 版本： 1.0.0
         * 版权： 中国银联<br>
         * 说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考，不提供编码性能规范性等方面的保障<br>
         * 该接口参考文档位置：open.unionpay.com帮助中心 下载  产品接口规范  《网关支付产品接口规范》<br>
         *              《平台接入接口规范-第5部分-附录》（内包含应答码接口规范，全渠道平台银行名称-简码对照表）<br>
         * 测试过程中的如果遇到疑问或问题您可以：1）优先在open平台中查找答案：
         * 							        调试过程中的问题或其他问题请在 https://open.unionpay.com/ajweb/help/faq/list 帮助中心 FAQ 搜索解决方案
         *                             测试过程中产生的6位应答码问题疑问请在 https://open.unionpay.com/ajweb/help/respCode/respCodeList 输入应答码搜索解决方案
         *                          2） 咨询在线人工支持： open.unionpay.com注册一个用户并登陆在右上角点击“在线客服”，咨询人工QQ测试支持。
         * 交易说明： 1）以后台通知或交易状态查询交易（Form_6_5_Query）确定交易成功，建议发起查询交易的机制：可查询N次（不超过6次），每次时间间隔2N秒发起,即间隔1，2，4，8，16，32S查询（查询到03，04，05继续查询，否则终止查询）
         *        2）退货金额不超过总金额，可以进行多次退货
         *        3）退货能对11个月内的消费做（包括当清算日），支持部分退货或全额退货，到账时间较长，一般1-10个清算日（多数发卡行5天内，但工行可能会10天），所有银行都支持
         */

        initSDKConfiguration(payment);

        String timeStart = DateUtils.formatDate(new Date(),"yyyyMMddHHmmss");

        //String timeExpire = DateUtils.formatDate(expireTime,"yyyyMMddHHmmss");

        Integer totalFee =  refundAmount.multiply(new BigDecimal(100)).intValue();

        PayNotify notify = new PayNotify();

        notify.setHasError(true);

        Map<String, String> param = new HashMap<String, String>();
        param.put("version",SDKConfig.getVersion());
        param.put("encoding",SDKConfig.getEncoding());
        //param.put("certId",CertUtil.getSignCertId()); //证书ID

        param.put("txnType","04");  //交易类型
        param.put("txnSubType","00");   //交易子类
        param.put("bizType",SDKConfig.getBizType());
        param.put("signMethod",SDKConfig.getSignMethod());
        param.put("channelType","07");//渠道类型
        param.put("accessType",SDKConfig.getAccessType());

        param.put("backUrl",SDKConfig.getRefundBackUrl());   //后台通知地址
        param.put("currencyCode", "156");
        param.put("origQryId",queryId);//原消费的queryId，可以从查询接口或者通知接口中获取，此处默认取demo演示页面传递的参数
        param.put("merId",SDKConfig.getMerId());    //商户号，请改自己的测试商户号，此处默认取demo演示页面传递的参数
        param.put("orderId",refundId);   //商户订单号，8-32位数字字母，不能含“-”或“_”，此处默认取demo演示页面传递的参数，可以自行定制规则
        param.put("txnTime",timeStart);//订单发送时间，格式为YYYYMMDDhhmmss，取北京时间，此处默认取demo演示页面传递的参数，参考取法： DateTime.Now.ToString("yyyyMMddHHmmss")
        param.put("txnAmt",totalFee.toString()); //交易金额，单位分，此处默认取demo演示页面传递的参数


        /*
        /**请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文------------->
        Map<String, String> reqData  = AcpService.sign(param,SDKConfig.getEncoding());//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
        String url = SDKConfig.getBackTransUrl();//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl

        Map<String, String> rspData = AcpService.post(reqData,url,SDKConfig.getEncoding());//这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

        LogUtil.writeMessage("银联无线支付退款参数："+ JsonConverter.toJson(rspData));

        /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->
        //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
        if(!rspData.isEmpty()){
            if(AcpService.validate(rspData, SDKConfig.getEncoding())){
                LogUtil.writeLog("验证签名成功");
                String respCode = rspData.get("respCode");
                if("00".equals(respCode)){
                    //交易已受理，等待接收后台通知更新订单状态,也可以主动发起 查询交易确定交易状态。
                    //TODO
                    String refund_no = rspData.get("orderId");

                    //交易成功并在页面返回success
                    notify.setHasError(false);
                    notify.setPayOrderNo(refund_no);
                    notify.setExtandProperties(rspData);

                }else if("03".equals(respCode)||
                        "04".equals(respCode)||
                        "05".equals(respCode)){
                    //后续需发起交易状态查询交易确定交易状态
                    //TODO
                    notify.setErrorMessage("银联无线支付退款处理超时，请稍微查询。");
                }else{
                    //其他应答码为失败请排查原因
                    //TODO

                    notify.setErrorMessage("银联无线支付退款失败：" + rspData.get("respMsg") + "。");
                }
            }else{
                LogUtil.writeErrorLog("验证签名失败");
                //TODO 检查验证签名失败的原因
                notify.setErrorMessage("银联无线支付退款商户端验证返回报文签名失败。");
            }
        }else{
            //未返回正确的http状态
            LogUtil.writeErrorLog("未获取到返回报文或返回http状态码非200");
            notify.setErrorMessage("银联无线支付退款请求失败，未获取到返回报文或返回http状态码非200");
        }*/

        SDKUtil.sign(param, SDKConfig.getEncoding());  // 签名

        String url = SDKConfig.getBackTransUrl();

        HttpClient hc = new HttpClient(url,10000,30000);
        int status =500;
        try {
            status = hc.send(param, SDKConfig.getEncoding());
        }
        catch (Exception exp)
        {
            LogUtil.writeErrorLog(exp.toString());
        }
        String result = hc.getResult();

        LogUtil.writeMessage("银联无线支付退款参数："+JsonConverter.toJson(param));

        if (status == 200)
        {
            Map<String, String> resData = SDKUtil.convertResultStringToMap(result);

            if (AcpService.validate(resData, SDKConfig.getEncoding()))
            {
                String respcode = resData.get("respCode"); //其他应答参数也可用此方法获取
                if ("00" == respcode)
                {
                    //交易已受理，等待接收后台通知更新订单状态，如果通知长时间未收到也可发起交易状态查询
                    //TODO
                    String refund_no = resData.get("orderId");

                    //交易成功并在页面返回success
                    notify.setHasError(false);
                    notify.setPayOrderNo(refund_no);
                    notify.setExtandProperties(resData);
                }
                else if ("03" == respcode ||
                        "04" == respcode ||
                        "05" == respcode)
                {
                    //后续需发起交易状态查询交易确定交易状态
                    //TODO
                    notify.setErrorMessage("银联无线支付退款处理超时，请稍微查询。");
                }
                else
                {
                    //其他应答码做以失败处理
                    //TODO
                    notify.setErrorMessage("银联无线支付退款失败：" + resData.get("respMsg") + "。");
                }
            }
            else
            {
                notify.setErrorMessage("银联无线支付退款商户端验证返回报文签名失败。");
            }
        }
        else
        {
            notify.setErrorMessage("银联无线支付退款请求失败，http状态：" + status + "。");
        }
        LogUtil.writeMessage(notify.getErrorMessage());
        return notify;
    }

    private Map<String,String> getSortMap(Map<String, String> reqParam)
    {
        Map<String, String> valideData = null;
        try {
            if (null != reqParam && !reqParam.isEmpty()) {
                Iterator<Map.Entry<String, String>> it = reqParam.entrySet().iterator();
                valideData = new HashMap<String, String>(reqParam.size());
                while (it.hasNext()) {
                    Map.Entry<String, String> e = it.next();
                    String key = (String) e.getKey();
                    String value = (String) e.getValue();
                    value = new String(value.getBytes(SDKConfig.getEncoding()), SDKConfig.getEncoding());
                    valideData.put(key, value);
                }
            }
        }
        catch (IOException exp)
        {
            exp.printStackTrace();
        }
        return valideData;
    }

    public PayNotify refundNotify(Map<String, String> resData)
    {
        PayNotify notify = new PayNotify();

        notify.setHasError(true);

        String payment = "unionpay_fctwap";
        initSDKConfiguration(payment);

        if (AcpService.validate(resData, SDKConfig.getEncoding()))
        {
            String respcode = resData.get("respCode"); //其他应答参数也可用此方法获取
            if ("00" == respcode)
            {
                //交易已受理，等待接收后台通知更新订单状态，如果通知长时间未收到也可发起交易状态查询
                //TODO
                String refund_no = resData.get("orderId");

                //交易成功并在页面返回success
                notify.setHasError(false);
                notify.setPayOrderNo(refund_no);
                notify.setExtandProperties(resData);

                 /*RefundManager.Instance.RefundSuccess(refund_no.ConvertToInt32(0),
                        JN.Core.Json.JsonSerializer.Serialize(notify.ExtandProperties));*/
            }
            else if ("03" == respcode ||
                    "04" == respcode ||
                    "05" == respcode)
            {
                //后续需发起交易状态查询交易确定交易状态
                //TODO
                notify.setErrorMessage("银联无线支付退款处理超时，请稍微查询。");
            }
            else
            {
                //其他应答码做以失败处理
                //TODO
                notify.setErrorMessage("银联无线支付退款失败：" + resData.get("respMsg") + "。");
            }
        }
        else
        {
            notify.setErrorMessage("银联无线支付退款商户端验证返回报文签名失败。");
        }

        return notify;
    }

    /***
    /// <summary>
    /// 获取异步通知支付平台方式
    /// </summary>
    /// <param name="dic"></param>
    /// <returns></returns>**/
    private String getNotifyPayment(Map<String,Object> dic)
    {
        String[] arrAppId = payConfig.getPlatform_ids().get("unionpay_merids").split("|");
        for (int i = 0; i < arrAppId.length; i++)
        {
            if (arrAppId[i].contains(dic.get("merid").toString()))
            {
                return arrAppId[i].split("#")[0];
            }
        }
        return "";
    }

}

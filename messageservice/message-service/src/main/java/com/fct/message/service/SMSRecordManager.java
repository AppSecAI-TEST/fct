package com.fct.message.service;

import com.fct.message.data.entity.SMSRecord;
import com.fct.message.data.repository.SMSRecordRepository;
import com.fct.message.service.qcloud.SmsMultiSender;
import com.fct.message.service.qcloud.SmsMultiSenderResult;
import com.fct.message.service.qcloud.SmsSingleSender;
import com.fct.message.service.qcloud.SmsSingleSenderResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jon on 2017/5/6.
 */
@Service
public class SMSRecordManager {
    @Autowired
    private SMSRecordRepository smsRecordRepository;

    //请根据实际 appid 和 appkey 进行开发，以下只作为演示 sdk 使用

    @Autowired
    private QCloudSMSConfig qCloudSMSConfig;

    public void send(String cellPhone,String content,String ip,String action) {
        if (StringUtils.isEmpty(cellPhone)) {
            throw new IllegalArgumentException("手机号码为空。");
        }
        if (StringUtils.isEmpty(content)) {
            throw new IllegalArgumentException("短信内容为空。");
        }
        if (StringUtils.isEmpty(ip)) {
            throw new IllegalArgumentException("ip为空。");
        }
        if (StringUtils.isEmpty(action)) {
            throw new IllegalArgumentException("短信类型为空。");
        }

        //发送短信
        SMSRecord sr = new SMSRecord();
        sr.setCellPhone(cellPhone);
        sr.setContent(content);
        sr.setCreateTime(new Date());
        sr.setAction(action);
        sr.setIp(ip);

        //调用发短信api
        Boolean send = false;
        String[] arrPhone = cellPhone.split(",");
        if(arrPhone.length>1)
        {
            ArrayList<String> arrayList = new ArrayList<>();
            for (String phone: arrPhone
                 ) {
                arrayList.add(phone);
            }
            send = multiSender(arrayList,content);
        }
        else
        {
            send = singleSender(cellPhone,content);
        }
        if(send)
        {
            sr.setIsSend(1);
            sr.setSendTime(new Date());
        }
        else
        {
            sr.setIsSend(0);
        }
        smsRecordRepository.save(sr);
    }

    private boolean multiSender(ArrayList<String> phoneNumbers,String content)
    {
        try {
            // 初始化群发
            SmsMultiSender multiSender = new SmsMultiSender(qCloudSMSConfig.getAppId(), qCloudSMSConfig.getAppKey());
            SmsMultiSenderResult multiSenderResult = multiSender.send(0, "86", phoneNumbers,
                    content, "", "");

            if(multiSenderResult.result == 0)
            {
                return true;
            }
            else
            {
                Constants.logger.warn(multiSenderResult.errMsg);
            }

        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return false;
    }
    private boolean singleSender(String cellphone,String content)
    {
        try {
            //初始化单发
            SmsSingleSender singleSender = new SmsSingleSender(qCloudSMSConfig.getAppId(), qCloudSMSConfig.getAppKey());
            SmsSingleSenderResult singleSenderResult = singleSender.send(0, "86", cellphone,
                    content, "", "");

            if(singleSenderResult.result == 0)
            {
                return true;
            }
            else
            {
                Constants.logger.warn(singleSenderResult.errMsg);
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return false;

    }

    /***
     *
     * //拉取短信回执和回复
     SmsStatusPuller pullstatus = new SmsStatusPuller(appid,appkey);
     SmsStatusPullCallbackResult callback_result = pullstatus.pullCallback(10);
     System.out.println(callback_result);
     SmsStatusPullReplyResult reply_result = pullstatus.pullReply(10);
     System.out.println(reply_result);

     // 发送通知内容
     SmsVoicePromptSender smsVoicePromtSender = new SmsVoicePromptSender(appid, appkey);
     SmsVoicePromptSenderResult smsSingleVoiceSenderResult = smsVoicePromtSender.send("86", phoneNumber1, 2,2,"欢迎使用", "");
     System.out.println(smsSingleVoiceSenderResult);

     //语音验证码发送
     SmsVoiceVerifyCodeSender smsVoiceVerifyCodeSender = new SmsVoiceVerifyCodeSender(appid,appkey);
     SmsVoiceVerifyCodeSenderResult smsVoiceVerifyCodeSenderResult = smsVoiceVerifyCodeSender.send("86",phoneNumber1, "123",2,"");
     System.out.println(smsVoiceVerifyCodeSenderResult);
     *
     * */
}

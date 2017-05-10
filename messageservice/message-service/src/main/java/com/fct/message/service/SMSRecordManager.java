package com.fct.message.service;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.BatchSmsAttributes;
import com.aliyun.mns.model.MessageAttributes;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.TopicMessage;
import com.fct.common.logger.LogService;
import com.fct.message.data.entity.SMSRecord;
import com.fct.message.data.repository.SMSRecordRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/6.
 */
@Service
public class SMSRecordManager {
    @Autowired
    private SMSRecordRepository smsRecordRepository;

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
        sr.setIsSend(1);
        sr.setSendTime(new Date());
        sr.setIp(ip);
        smsRecordRepository.save(sr);

        //调用发短信api

        //aliyunSendSMS();

    }

    private void aliyunSendSMS(List<String> lsCellphone)
    {
        /**
         * Step 1. 获取主题引用
         */
        CloudAccount account = new CloudAccount("$YourAccessId", "$YourAccessKey", "$YourMNSEndpoint");
        MNSClient client = account.getMNSClient();
        CloudTopic topic = client.getTopicRef("$YourTopic");
        /**
         * Step 2. 设置SMS消息体（必须）
         *
         * 注：目前暂时不支持消息内容为空，需要指定消息内容，不为空即可。
         */
        RawTopicMessage msg = new RawTopicMessage();
        msg.setMessageBody("sms-message");
        /**
         * Step 3. 生成SMS消息属性
         */
        MessageAttributes messageAttributes = new MessageAttributes();
        BatchSmsAttributes batchSmsAttributes = new BatchSmsAttributes();
        // 3.1 设置发送短信的签名（SMSSignName）
        batchSmsAttributes.setFreeSignName("方寸堂");
        // 3.2 设置发送短信使用的模板（SMSTempateCode）
        batchSmsAttributes.setTemplateCode("SMS_888888");
        // 3.4 增加接收短信的号码
        for (String cellphone:lsCellphone
             ) {
            // 3.3 设置发送短信所使用的模板中参数对应的值（在短信模板中定义的，没有可以不用设置）
            BatchSmsAttributes.SmsReceiverParams smsReceiverParams = new BatchSmsAttributes.SmsReceiverParams();
            smsReceiverParams.setParam("$YourSMSTemplateParamKey1", "$value1");
            batchSmsAttributes.addSmsReceiver(cellphone, smsReceiverParams);
        }
        messageAttributes.setBatchSmsAttributes(batchSmsAttributes);
        try {
            /**
             * Step 4. 发布SMS消息
             */
            TopicMessage ret = topic.publishMessage(msg, messageAttributes);
            LogService.info("MessageId: " + ret.getMessageId());
            //System.out.println("MessageId: " + ret.getMessageId());
            //System.out.println("MessageMD5: " + ret.getMessageBodyMD5());
        } catch (ServiceException se) {
            //System.out.println(se.getErrorCode() + se.getRequestId());
            //System.out.println(se.getMessage());
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        client.close();
    }
}

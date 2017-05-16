package com.fct.message.service;

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

    }
}

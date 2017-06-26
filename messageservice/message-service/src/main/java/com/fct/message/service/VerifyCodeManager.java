package com.fct.message.service;

import com.fct.core.utils.DateUtils;
import com.fct.message.data.entity.VerifyCode;
import com.fct.message.data.repository.VerifyCodeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;

/**
 * Created by jon on 2017/5/6.
 * jon love nancy
 */
@Service
public class VerifyCodeManager {

    @Autowired
    private VerifyCodeRepository verifyCodeRepository;

    @Autowired
    private SMSRecordManager smsRecordManager;

    @Transactional
    public String create(String sessionId,String cellPhone)
    {
        if(StringUtils.isEmpty(sessionId))
        {
            throw new IllegalArgumentException("sessionId为空。");
        }

        //校验是否短信获取太频繁
        Date endtime = DateUtils.parseString(DateUtils.addDay(new Date(),1).toString(),"yyyy-MM-dd");
        if(verifyCodeRepository.getCountByToday(cellPhone,new Date().toString(),
                endtime.toString())>30)
        {
            throw new IllegalArgumentException("当天接收验证码次数超过限制!");
        }
        if(verifyCodeRepository.getCountBySession(sessionId,cellPhone)>5)
        {
            throw new IllegalArgumentException("当天接收验证码次数超过限制!");
        }
        VerifyCode vc  = verifyCodeRepository.getCode(sessionId,cellPhone,new Date().toString());
        if(vc!=null) {
            //同一个sessionId和手机号码.1分钟类只能提交1次,
            if (DateUtils.minutesBetween(vc.getCreateTime(),new Date()) <= 1) {
                throw new IllegalArgumentException("请稍等1分钟后再试。");
            }
            return vc.getCode();
            /*
            //将sessionId和手机号码相同的数据过期时间设置为过期.
            vc.setExpireTime(DateUtils.addDay(new Date(), -1)); //过期
            verifyCodeRepository.saveAndFlush(vc);*/
        }
        else
        {
            Integer ranCode = new Random().nextInt(999999);
            vc = new VerifyCode();
            vc.setSessionId(sessionId);
            vc.setCellPhone(cellPhone);
            vc.setCode(ranCode.toString());
            vc.setExpireTime(DateUtils.addMinute(new Date(),30));
            vc.setCreateTime(new Date());

            verifyCodeRepository.save(vc);
        }
        return vc.getCode();
    }

    public int check(String sessionId,String cellPhone,String code)
    {
        return verifyCodeRepository.check(sessionId,cellPhone,code,new Date().toString());
    }



}

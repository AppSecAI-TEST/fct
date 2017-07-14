package com.fct.promotion.service.business;

import com.fct.core.utils.StringHelper;
import com.fct.promotion.data.entity.CouponSpareCode;
import com.fct.promotion.data.repository.CouponSpareCodeRepository;
import com.fct.promotion.service.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

/**
 * Created by jon on 2017/5/12.
 */
@Service
public class CouponSpareCodeManager {

    static Object syncObj = new Object();

    @Autowired
    private CouponSpareCodeRepository couponSpareCodeRepository;

    public String getValidCode()
    {
        CouponSpareCode obj = couponSpareCodeRepository.getTopOne();
        couponSpareCodeRepository.updateByCode(obj.getCode());

        return obj.getCode();
    }

    public void setCodeUsed(String code)
    {
        CouponSpareCode obj = couponSpareCodeRepository.findOneByCode(code);

        if (obj == null)
        {
            throw new IllegalArgumentException("券码无效");
        }

        obj.setStatus(2);
        obj.setLastUpdateTime(new Date());
        couponSpareCodeRepository.saveAndFlush(obj);
    }

    void addCode(String code)
    {
        CouponSpareCode obj = new CouponSpareCode();
        obj.setCode(code);
        obj.setStatus(0);
        obj.setCreateTime(new Date());
        obj.setLastUpdateTime(new Date());
        couponSpareCodeRepository.save(obj);
    }

    Integer getCount()
    {
        return couponSpareCodeRepository.getCount();
    }

    int limitCount = 100000;

    //优惠券空闲编码生成服务
    public void task()
    {
        try
        {
            int currentSpareCodeCount = getCount();
            if (currentSpareCodeCount >= limitCount)
            {
                return;
            }
            int count = Math.min(10000, limitCount - currentSpareCodeCount);
            int i = 0;
            while (i < count)
            {
                String code = StringHelper.getRandomNumber(8);
                try
                {
                    //数据库唯一键约束保证券码不重复
                    addCode(code);
                }
                catch (Exception exp)
                {
                    Constants.logger.info("插入空闲券码出错：" + exp.toString());
                }
                i++;
                if (i % 100 == 0)
                {
                    Thread.sleep(3000);
                }
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
}

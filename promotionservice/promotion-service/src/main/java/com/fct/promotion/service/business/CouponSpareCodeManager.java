package com.fct.promotion.service.business;

import com.fct.common.exceptions.BaseException;
import com.fct.promotion.data.entity.CouponSpareCode;
import com.fct.promotion.data.repository.CouponSpareCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by jon on 2017/5/12.
 */
@Service
public class CouponSpareCodeManager {

    static Object syncObj = new Object();

    @Autowired
    CouponSpareCodeRepository couponSpareCodeRepository;

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
            throw new BaseException("券码无效");
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
}

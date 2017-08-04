package com.fct.promotion.service.business;

import com.fct.promotion.data.entity.CouponUseLog;
import com.fct.promotion.data.repository.CouponUseLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by jon on 2017/5/13.
 */
@Service
public class CouponUseLogManager {
    @Autowired
    CouponUseLogRepository couponUseLogRepository;


    public CouponUseLog add(CouponUseLog obj)
    {
        return save(obj);
    }

    private CouponUseLog save(CouponUseLog obj)
    {
        obj.setCreateTime(new Date());
        couponUseLogRepository.save(obj);
        return obj;
    }
}

package com.fct.promotion.service.business;

import com.fct.promotion.data.entity.CouponOperateLog;
import com.fct.promotion.data.repository.CouponOperateLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by jon on 2017/5/12.
 */
@Service
public class CouponOperateLogManager {

    @Autowired
    CouponOperateLogRepository couponOperateLogRepository;

    void add(CouponOperateLog log)
    {
        log.setOperateTime(new Date());
        couponOperateLogRepository.save(log);
    }
}

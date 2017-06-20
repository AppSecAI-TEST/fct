package com.fct.promotion.service.business;

import com.fct.promotion.data.entity.DiscountUseLog;
import com.fct.promotion.data.repository.DiscountUseLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/13.
 */
@Service
public class DiscountUseLogManager {

    @Autowired
    private DiscountUseLogRepository discountUseLogRepository;

    @Transactional
    public void add(List<DiscountUseLog> lst)
    {
        for (DiscountUseLog obj:lst
             ) {
            save(obj);
        }
    }

    private void save(DiscountUseLog obj)
    {
        if (obj.getId() > 0)
        {
            throw new IllegalArgumentException("use log can not update");
            //entityContext.Update<CouponUseLog>(obj);
        }
        else
        {
            obj.setCreateTime(new Date());
            discountUseLogRepository.save(obj);
        }
    }
}

package com.fct.finance.service.business;

import com.fct.finance.data.entity.PayPlatform;
import com.fct.finance.data.repository.PayPlatformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jon on 2017/4/21.
 */
@Service
public class PayPlatformManager {
    @Autowired
    private PayPlatformRepository payPlatformRepository;

    public List<PayPlatform> findAll(String payment)
    {
        return payPlatformRepository.findByCode("%"+payment+"%");
    }
}

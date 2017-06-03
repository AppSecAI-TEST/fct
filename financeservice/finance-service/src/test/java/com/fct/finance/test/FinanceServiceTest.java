package com.fct.finance.test;

import com.fct.finance.data.entity.PayPlatform;
import com.fct.finance.interfaces.FinanceService;
import com.fct.finance.service.ApplicationStartUp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;


/**
 * Created by jon on 2017/6/1.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationStartUp.class)
@ActiveProfiles("de")
@WebAppConfiguration
public class FinanceServiceTest {

    @Autowired
    private FinanceService financeService;

    @Test
    public void findMemberAccount(){
        List<PayPlatform> ls = financeService.findPayPlatform();
        Assert.assertNotNull(ls);
    }
}
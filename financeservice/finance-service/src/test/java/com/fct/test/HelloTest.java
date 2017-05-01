package com.fct.test;

import com.fct.finance.service.ApplicationStartUp;
import com.fct.finance.service.FinanceServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by jon on 2017/4/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationStartUp.class})
@ActiveProfiles(value = "de")
public class HelloTest {

    @Autowired
    private FinanceServiceImpl financeService;

    @Test
    public void testService(){
//        financeService.sayHello();
    }
}

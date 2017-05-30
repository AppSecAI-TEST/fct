package com.fct.mall.test;

import com.fct.mall.data.entity.GoodsGrade;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.service.StartupApplication;
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
 * Created by ningyang on 2017/5/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StartupApplication.class)
@ActiveProfiles("de")
@WebAppConfiguration
public class MallServiceTest {

    @Autowired
    private MallService mallService;

    @Test
    public void getAllGrade(){
        List<GoodsGrade> goodsGrades = mallService.findGoodsGrade();
        Assert.assertNotNull(goodsGrades);
    }
}

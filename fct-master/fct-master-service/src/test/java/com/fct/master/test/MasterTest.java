package com.fct.master.test;

import com.fct.master.dto.MasterBrief;
import com.fct.master.interfaces.MasterService;
import com.fct.master.service.startup.ApplicationStartup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by nick on 2017/5/29.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationStartup.class})
@ActiveProfiles("de")
public class MasterTest {

    @Autowired
    private MasterService masterService;

    @Test
    public void getBrief(){
        Integer masterId = 1;
        MasterBrief masterBrief = masterService.getMasterBrief(masterId);
        if(masterBrief!=null){
            System.out.println(masterBrief.getBrief());
        }
    }
}

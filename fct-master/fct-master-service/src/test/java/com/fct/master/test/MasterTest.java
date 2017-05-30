package com.fct.master.test;

import com.fct.master.dto.MasterBrief;
import com.fct.master.dto.MasterDto;
import com.fct.master.dto.MasterNewsResponse;
import com.fct.master.dto.MasterSerialsDto;
import com.fct.master.interfaces.MasterService;
import com.fct.master.service.domain.Master;
import com.fct.master.service.startup.ApplicationStartup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

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

    @Test
    public void insertMaster(){
        MasterDto master = new MasterDto();
        master.setBrief("哈哈");
        master.setCoverUrl("www.baidu.com");
        master.setMasterName("毛三毛");
        master.setProfile("你是傻逼");
        master.setTitle("一級智障");
        master.setUpdateTime(new Date());
        master.setWorksCount(99L);
        masterService.insertMaster(master);
    }

    @Test
    public void masterTest(){
        Integer masterId = 1;
        MasterNewsResponse response = masterService.getMasterNewsResponse(masterId, 0, 20);
        System.out.println(response.getCount());
    }

    @Test
    public void masterCommonDataTest(){
        Integer masterId = 1;
        MasterSerialsDto masterSerialsDto = masterService.getMasterCommonData(masterId);
        System.out.println(masterSerialsDto.getMasterLive().getChannelId());
    }
}

package com.fct.master.test;

import com.fct.master.dto.*;
import com.fct.master.interfaces.MasterService;
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

    /**
     * 获取大师简介
     */
    @Test
    public void getBrief(){
        Integer masterId = 1;
        MasterBrief masterBrief = masterService.getMasterBrief(masterId);
        if(masterBrief!=null){
            System.out.println(masterBrief.getBrief());
        }
    }

    /**
     * 添加大师
     */
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

    /**
     * 获取大师动态分页
     */
    @Test
    public void masterTest(){
        Integer masterId = 1;
        PageResponse<MasterNewsDto> pageResponse = masterService.getMasterNewsResponse(masterId, 0, 20);
        System.out.println(pageResponse.getTotalCount());
    }

    /**
     * 大师公共数据
     */
    @Test
    public void masterCommonDataTest(){
        Integer masterId = 1;
        MasterSerialsDto masterSerialsDto = masterService.getMasterCommonData(masterId);
        System.out.println(masterSerialsDto.getMasterLive().getChannelId());
    }

    /**
     * 获取大师列表分页
     */
    @Test
    public void getMasterListTest(){
        PageResponse<MasterDto> pageResponse = masterService.getMasters(0, 20);
        System.out.println(pageResponse.getElements().size());
    }
}

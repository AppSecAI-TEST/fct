package com.fct.master.service;

import com.fct.master.dto.MasterDto;
import com.fct.master.interfaces.MasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nick on 2017/5/25.
 */
@RestController
@RequestMapping("/api/master")
public class MasterController {

    @Autowired
    private MasterService masterService;

    @PostMapping(value = "save")
    public MasterDto addMaster(@RequestBody MasterDto masterDto){
        return masterService.insertMaster(masterDto);
    }
}

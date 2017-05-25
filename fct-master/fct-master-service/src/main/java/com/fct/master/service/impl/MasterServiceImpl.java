package com.fct.master.service.impl;


import com.fct.master.dto.MasterDto;
import com.fct.master.dto.MasterResponse;
import com.fct.master.interfaces.MasterService;
import com.fct.master.service.domain.Master;
import com.fct.master.service.repository.MasterRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by nick on 2017/5/25.
 */
@Service
public class MasterServiceImpl implements MasterService {

    @Autowired
    private MasterRepository masterRepository;

    @Override
    public MasterDto insertMaster(MasterDto masterDto) {
        Master master = new Master(masterDto);
        master = masterRepository.save(master);
        masterDto.setMasterId(master.getMasterId());
        return masterDto;
    }

    @Override
    public MasterResponse getMasters(int start, int size) {
        MasterResponse response = new MasterResponse();
        boolean hasMore = true;
        Long count = masterRepository.countAllByDelFlag(0);
        int end = start + size;
        if(end >= count){
            hasMore = false;
            end = count.intValue();
        }
        List<Master> masters = masterRepository.getAllMaster(start, end);
        response.setHasMore(hasMore);
        response.setCurrent(end);
        response.setAllCount(count);
        response.setMasters(getMasterDtos(masters));
        return response;
    }

    private List<MasterDto> getMasterDtos(List<Master> masters){
        List<MasterDto> masterDtos = Lists.newArrayList();
        if(masters!=null && masters.size()>0){
            for(Master master: masters){
                MasterDto masterDto = new MasterDto();
                masterDto.setBrief(master.getBrief());
                masterDto.setCoverUrl(master.getCoverUrl());
                masterDto.setDelflag(master.getDelFlag());
                masterDto.setMasterId(master.getMasterId());
                masterDto.setProfile(master.getProfile());
                masterDto.setMasterName(master.getMasterName());
                masterDto.setUpdateTime(master.getUpdateTime());
                masterDto.setTitle(master.getTitle());
                masterDto.setWorksCount(master.getWorksCount());
                masterDtos.add(masterDto);
            }
        }
        return masterDtos;
    }

    @Override
    public void openMasterLive() {

    }
}

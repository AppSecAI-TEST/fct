package com.fct.master.service.impl;


import com.fct.master.dto.MasterBrief;
import com.fct.master.dto.MasterDto;
import com.fct.master.dto.MasterResponse;
import com.fct.master.interfaces.MasterService;
import com.fct.master.service.domain.Master;
import com.fct.master.service.repository.MasterRepository;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by nick on 2017/5/25.
 */
@Service
public class MasterServiceImpl implements MasterService {

    @Autowired
    private MasterRepository masterRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Override
    public MasterBrief getMasterBrief(int masterId) {
        String sql1 = String.format("select a.master_id, a.brief, a.cover_url from master a where a.master_id = %d and a.del_flag = 0", masterId);
        String sql2 = String.format("select count(*) as count from attention a where a.master_id = %d and a.del_flag = 0", masterId);
        Map<String, Object> results1 = jdbcTemplate.queryForMap(sql1);
        Map<String, Object> results2 = jdbcTemplate.queryForMap(sql2);
        MasterBrief masterBrief = new MasterBrief();
        if(results1!=null&&results1.size()>0){
            int id = ((Long) results1.get("master_id")).intValue();
            String coverUrl = (String) results1.get("cover_url");
            String brief = (String) results1.get("brief");
            Long count = (Long) results2.get("count");
            Double attentionCount = 0.0d;
            if(count>10000){
                attentionCount = count/10000d;
                masterBrief.setAttentionCount(String.valueOf(attentionCount));
            }else{
                masterBrief.setAttentionCount(String.valueOf(attentionCount.intValue()));
            }
            masterBrief.setBrief(brief);
            masterBrief.setMasterId(id);
            masterBrief.setCoverUrl(coverUrl);
        }
        return masterBrief;
    }
}

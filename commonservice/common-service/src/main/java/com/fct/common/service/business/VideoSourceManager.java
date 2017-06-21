package com.fct.common.service.business;

import com.fct.common.data.entity.VideoSource;
import com.fct.common.data.repository.VideoSourceRepository;
import com.fct.common.interfaces.PageResponse;
import com.fct.common.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class VideoSourceManager {

    @Autowired
    private VideoSourceRepository videoSourceRepository;

    @Autowired
    private JdbcTemplate jt;

    public Integer countByCategoryId(Integer cateId)
    {
        return videoSourceRepository.countByCategoryId(cateId);
    }

    public VideoSource save(VideoSource videoSource)
    {
        videoSource.setGuid(UUID.randomUUID().toString());
        return videoSourceRepository.save(videoSource);
    }

    public VideoSource findById(String guid)
    {
        return videoSourceRepository.findOne(guid);
    }

    public List<VideoSource> findByGuid(String guids)
    {
        String sql = String.format("SELECT * FROM VideoSource WHERE Status=1 AND guid in (%s)",guids);

        return jt.query(sql,new Object[]{}, new BeanPropertyRowMapper<>(VideoSource.class));
    }

    public void updateStatus(String guid)
    {
        videoSourceRepository.updateStatus(guid);
    }

    private String getCondition(String name, Integer categoryId, Integer status, String fileType,
                                String startTime, String endTime,List<Object> param) {
        String condition = "";
        if (!StringUtils.isEmpty(name)) {
            condition += " AND name like ?";
            param.add("%" + name + "%");
        }
        if (categoryId > 0) {
            condition += " AND categoryId=" + categoryId;
        }
        if (status > -1) {
            condition += " AND Status=" + status;
        }
        if (!StringUtils.isEmpty(fileType))
        {
            condition +=" AND filetype=?";
            param.add(fileType);
        }
        if(!StringUtils.isEmpty(startTime))
        {
            condition += " AND createTime >=?";
            param.add(startTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            condition += " AND endTime <?";
            param.add(endTime);
        }
        return condition;
    }

    public PageResponse<VideoSource> findAll(String name, Integer categoryId, Integer status, String fileType,
                                             String startTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="VideoSource";
        String field ="*";
        String orderBy = "sortIndex asc";
        String condition= getCondition(name,categoryId,status,fileType,startTime,endTime,param);

        String sql = "SELECT Count(0) FROM VideoSource WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<VideoSource> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<>(VideoSource.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<VideoSource> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;
    }
}

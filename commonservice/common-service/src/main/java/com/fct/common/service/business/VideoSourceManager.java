package com.fct.common.service.business;

import com.fct.common.service.QCloudConfig;
import com.fct.common.service.qcloud.*;

import com.fct.common.data.entity.VideoSource;
import com.fct.common.data.repository.VideoSourceRepository;
import com.fct.common.interfaces.PageResponse;
import com.fct.common.service.qcloud.Utilities.Json.JSONObject;
import com.fct.core.utils.PageUtil;
import com.fct.core.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class VideoSourceManager {

    @Autowired
    private VideoSourceRepository videoSourceRepository;

    @Autowired
    private JdbcTemplate jt;

    @Autowired
    private QCloudConfig qCloudConfig;

    public Integer countByCategoryId(Integer cateId)
    {
        return videoSourceRepository.countByCategoryId(cateId);
    }

    public VideoSource save(VideoSource videoSource)
    {
        videoSource.setGuid(UUID.randomUUID().toString());
        return videoSourceRepository.save(videoSource);
    }

    public String upload(VideoSource videoSource,byte[] fileByte)
    {
        String[] videoNameSplit = videoSource.getOriginalName().split("\\.");
        String fileType = videoNameSplit[videoNameSplit.length - 1];
        videoSource.setFileType(fileType);
        // 仅上传视频
        //VodApi.upload(secretId, secretKey, videoPath);
        // 同时上传视频和封面
        if(fileByte != null) {
            JSONObject result = VodApi.upload(qCloudConfig.getSecretId(), qCloudConfig.getSecretKey(), fileByte, fileType);

            videoSource.setUrl(result.getJSONObject("video").getString("storagePath"));
            if (StringUtils.isEmpty(videoSource.getImg())) {
                videoSource.setImg(result.getJSONObject("cover").getString("storagePath"));
            }
        }
        videoSource.setCreateTime(new Date());
        videoSource.setStatus(0);
        videoSource.setGuid(UUIDUtil.generateUUID());
        videoSourceRepository.save(videoSource);

        return videoSource.getGuid();
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
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(name)) {
            sb.append(" AND name like ?");
            param.add("%" + name + "%");
        }
        if (categoryId > 0) {
            sb.append(" AND categoryId=" + categoryId);
        }
        if (status > -1) {
            sb.append(" AND Status=" + status);
        }
        if (!StringUtils.isEmpty(fileType))
        {
            sb.append(" AND filetype=?");
            param.add(fileType);
        }
        if(!StringUtils.isEmpty(startTime))
        {
            sb.append(" AND createTime >=?");
            param.add(startTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            sb.append(" AND endTime <?");
            param.add(endTime);
        }
        return sb.toString();
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

package com.fct.artist.service.business;

import com.fct.artist.data.entity.ArtistDynamic;
import com.fct.artist.data.entity.ArtistLive;
import com.fct.artist.data.repository.ArtistDynamicRepository;
import com.fct.artist.interfaces.PageResponse;
import com.fct.core.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ArtistDynamicManager {

    @Autowired
    private ArtistDynamicRepository artistDynamicRepository;

    @Autowired
    private JdbcTemplate jt;

    public Integer save(ArtistDynamic artistDynamic)
    {
        if(artistDynamic.getArtistId()<=0)
        {
            throw new IllegalArgumentException("艺人id为空");
        }
        if(StringUtils.isEmpty(artistDynamic.getContent()))
        {
            throw new IllegalArgumentException("内容为空。");
        }
        if(artistDynamic.getId() == null || artistDynamic.getId()<=0)
        {
            artistDynamic.setCreateTime(new Date());
        }
        if(!StringUtils.isEmpty(artistDynamic.getVideoId()))
        {
            artistDynamic.setVideoImg(artistDynamic.getImages());
        }
        artistDynamic.setUpdateTime(new Date());
        artistDynamicRepository.save(artistDynamic);

        return artistDynamic.getId();
    }

    public void updateStatus(Integer id)
    {
        if(id==null || id<=0)
        {
            throw  new IllegalArgumentException("id 为空");
        }
        ArtistDynamic dynamic = artistDynamicRepository.findOne(id);
        dynamic.setStatus(1-dynamic.getStatus());
        dynamic.setUpdateTime(new Date());
        artistDynamicRepository.save(dynamic);
    }

    public ArtistDynamic findById(Integer id)
    {
        if(id==null || id<=0)
        {
            throw  new IllegalArgumentException("id 为空");
        }
        return artistDynamicRepository.findOne(id);
    }

    public PageResponse<ArtistDynamic> findAll(Integer artistId, String content, Integer status, String startTime, String endTime,
                                                         Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="ArtistDynamic";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getCondition(artistId,content,status,startTime,endTime,param);

        String sql = "SELECT Count(0) FROM ArtistDynamic WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<ArtistDynamic> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<>(ArtistDynamic.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<ArtistDynamic> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;
    }

    private String getCondition(Integer artistId, String content, Integer status, String startTime, String endTime,
                                List<Object> param)
    {
        StringBuilder sb = new StringBuilder();
        if(artistId>0)
        {
            sb.append(" AND artistId="+artistId);
        }
        if(!StringUtils.isEmpty(content))
        {
            sb.append(" AND content like ?");
            param.add("%"+content+"%");
        }
        if(status>-1)
        {
            sb.append(" AND Status="+status);
        }
        if(!StringUtils.isEmpty(startTime))
        {
            sb.append(" AND createTime>=?");
            param.add(startTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            sb.append(" AND createTime<?");
            param.add(endTime);
        }
        return sb.toString();
    }
}

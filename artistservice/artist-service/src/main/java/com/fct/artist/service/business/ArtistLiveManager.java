package com.fct.artist.service.business;

import com.fct.artist.data.entity.ArtistLive;
import com.fct.artist.data.repository.ArtistLiveRepository;
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
public class ArtistLiveManager {

    @Autowired
    private ArtistLiveRepository artistLiveRepository;

    @Autowired
    private JdbcTemplate jt;

    public Integer save(ArtistLive live)
    {
        if(live.getArtistId() <=0)
        {
            throw new IllegalArgumentException("艺人为空");
        }
        if(StringUtils.isEmpty(live.getLiveId()))
        {
            throw new IllegalArgumentException("直播地址为空");
        }
        if(StringUtils.isEmpty(live.getTitle()))
        {
            throw new IllegalArgumentException("标题为空");
        }
        if(live.getId() == null ||live.getId()<=0)
        {
            live.setCreateTime(new Date());
        }
        /*if(artistLiveRepository.countByArtistId(live.getArtistId())>0)
        {
            throw new IllegalArgumentException("艺人直播频道已存在。");
        }*/

        live.setUpdateTime(new Date());
        artistLiveRepository.save(live);

        return live.getId();
    }

    public ArtistLive findByArtist(Integer artistId)
    {
        if(artistId==null || artistId<=0)
        {
            throw  new IllegalArgumentException("artistId 为空");
        }
        return artistLiveRepository.findByArtistId(artistId);
    }

    public ArtistLive findById(Integer id)
    {
        if(id==null || id<=0)
        {
            throw  new IllegalArgumentException("Id 为空");
        }
        return artistLiveRepository.findOne(id);
    }

    public void updateStatus(Integer id)
    {
        if(id==null || id<=0)
        {
            throw  new IllegalArgumentException("id 为空");
        }
        ArtistLive live = artistLiveRepository.findOne(id);
        live.setStatus(1-live.getStatus());
        live.setUpdateTime(new Date());
        artistLiveRepository.save(live);
    }

    public PageResponse<ArtistLive> findAll(Integer artistId, Integer status, String startTime, String endTime,
                                            Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="ArtistLive";
        String field ="*";
        String orderBy = "startTime Desc";
        String condition= getCondition(artistId,status,startTime,endTime,param);

        String sql = "SELECT Count(0) FROM ArtistLive WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<ArtistLive> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<>(ArtistLive.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<ArtistLive> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;
    }

    private String getCondition(Integer artistId, Integer status, String startTime, String endTime,List<Object> param)
    {
        StringBuilder sb = new StringBuilder();
        if(artistId>0)
        {
            sb.append(" AND artistId="+artistId);
        }
        if(status>-1)
        {
            sb.append(" AND Status="+status);
        }
        if(!StringUtils.isEmpty(startTime))
        {
            sb.append(" AND startTime>=?");
            param.add(startTime);
        }
        if(!StringUtils.isEmpty(endTime))
        {
            sb.append(" AND endTime<=?");
            param.add(endTime);
        }
        return sb.toString();
    }
}

package com.fct.artist.service.business;

import com.fct.artist.data.entity.Artist;
import com.fct.artist.data.repository.ArtistRepository;
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
public class ArtistManager {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistGoodsManager artistGoodsManager;

    @Autowired
    private ArtistLiveManager artistLiveManager;

    @Autowired
    private JdbcTemplate jt;

    public void save(Artist artist)
    {
        if(StringUtils.isEmpty(artist.getName()))
        {
            throw new IllegalArgumentException("艺人名字为空。");
        }
        if(StringUtils.isEmpty(artist.getTitle()))
        {
            throw new IllegalArgumentException("头衔为空。");
        }
        if(StringUtils.isEmpty(artist.getBanner()))
        {
            throw new IllegalArgumentException("banner为空。");
        }
        if(StringUtils.isEmpty(artist.getDescription()))
        {
            throw new IllegalArgumentException("描述为空。");
        }
        if(StringUtils.isEmpty(artist.getHeadPortrait()))
        {
            throw new IllegalArgumentException("艺人头像为空。");
        }
        if(StringUtils.isEmpty(artist.getIntro()))
        {
            throw new IllegalArgumentException("艺人简介为空。");
        }
        if(StringUtils.isEmpty(artist.getMainImg()))
        {
            throw new IllegalArgumentException("主图为空。");
        }
        if(artist.getId() ==null || artist.getId() == 0)
        {
            artist.setCreateTime(new Date());
            artist.setFollowCount(0);
            artist.setViewCount(1000);
            artist.setGoodsCount(0);
        }
        artist.setUpdateTime(new Date());
        artistRepository.save(artist);
    }

    public Artist findById(Integer id)
    {
        if(id==null || id<=0)
        {
            throw  new IllegalArgumentException("id 为空");
        }
        Artist artist = artistRepository.findOne(id);
        artist.setGoods(artistGoodsManager.findByArtist(id));
        artist.setLive(artistLiveManager.findByArtist(id));
        return artist;
    }

    public void updateStatus(Integer id)
    {
        if(id==null || id<=0)
        {
            throw  new IllegalArgumentException("id 为空");
        }
        Artist artist = artistRepository.findOne(id);
        artist.setStatus(1-artist.getStatus());
        artist.setUpdateTime(new Date());
        artistRepository.save(artist);
    }

    public PageResponse<Artist> findAll(String name, Integer status, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="Artist";
        String field ="*";
        String orderBy = "sortIndex asc";
        String condition= getCondition(name,status,param);

        String sql = "SELECT Count(0) FROM Artist WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<Artist> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<>(Artist.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<Artist> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;
    }

    private String getCondition(String name, Integer status,List<Object> param)
    {
        String condition ="";
        if(!StringUtils.isEmpty(name))
        {
            condition +=" AND name like ?";
            param.add("%"+name+"%");
        }
        if(status>-1)
        {
            condition += " AND status="+status;
        }
        return condition;
    }
}

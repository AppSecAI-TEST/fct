package com.fct.artist.service.business;

import com.fct.artist.data.entity.ArtistComment;
import com.fct.artist.data.repository.ArtistCommentRepository;
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
public class ArtistCommentManager {

    @Autowired
    private ArtistCommentRepository artistCommentRepository;

    @Autowired
    private JdbcTemplate jt;

    public Integer save(ArtistComment artistComment)
    {
        if(artistComment.getMemberId() <=0)
        {
            throw new IllegalArgumentException("会员id不正确。");
        }
        if(artistComment.getArtistId()<=0)
        {
            throw new IllegalArgumentException("艺人不正确");
        }
        if(StringUtils.isEmpty(artistComment.getUserName()))
        {
            throw new IllegalArgumentException("用户名为空");
        }
        if(StringUtils.isEmpty(artistComment.getContent()))
        {
            throw new IllegalArgumentException("内容为空");
        }

        if(artistComment.getReplyId() == null)
        {
            artistComment.setReplyId(0);
        }

        if(artistComment.getId() == null || artistComment.getId()<=0)
        {
            artistComment.setCreateTime(new Date());
            artistComment.setStatus(0);
        }
        artistComment.setUpdateTime(new Date());
        artistCommentRepository.save(artistComment);
        return artistComment.getId();
    }

    public ArtistComment findById(Integer id)
    {
        if(id==null || id<=0)
        {
            throw  new IllegalArgumentException("id 为空");
        }
        return artistCommentRepository.findOne(id);
    }

    public void updateStatus(Integer id)
    {
        if(id==null || id<=0)
        {
            throw  new IllegalArgumentException("id 为空");
        }
        ArtistComment artistComment = artistCommentRepository.findOne(id);
        artistComment.setStatus(1-artistComment.getStatus());
        artistComment.setUpdateTime(new Date());
        artistCommentRepository.save(artistComment);
    }

    public PageResponse<ArtistComment> findAll(Integer artistId, Integer memberId, String username, Integer status,
                                                         Integer replyId, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="ArtistComment";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getCondition(artistId,memberId,username,status,replyId,param);

        String sql = "SELECT Count(0) FROM ArtistComment WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<ArtistComment> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<>(ArtistComment.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<ArtistComment> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;
    }

    private String getCondition(Integer artistId, Integer memberId, String username, Integer status,
                                Integer replyId,List<Object> param)
    {
        String condition="";
        if(artistId>0)
        {
            condition+=" AND artistId="+artistId;
        }
        if(memberId>0)
        {
            condition+=" AND memberId="+memberId;
        }
        if(!StringUtils.isEmpty(username))
        {
            condition+=" AND username like ?";
            param.add("%+ "+username+" +%");
        }
        if(status>-1)
        {
            condition+=" AND status="+status;
        }
        if(replyId>0)
        {
            condition += " AND replayId="+replyId;
        }
        return condition;
    }

}

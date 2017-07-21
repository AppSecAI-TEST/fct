package com.fct.artist.service.business;

import com.fct.artist.data.entity.Artist;
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

    public Integer reply(Integer id,Integer replyId,Integer memberId,String userName,String content)
    {

        if(memberId <=0)
        {
            throw new IllegalArgumentException("会员id不正确。");
        }
        if(StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("用户名为空");
        }
        if(StringUtils.isEmpty(content))
        {
            throw new IllegalArgumentException("内容为空");
        }

        ArtistComment comment = null;
        if(id>0)
        {
            comment = findById(id);
        }else
        {
            comment = new ArtistComment();
            comment.setCreateTime(new Date());
            comment.setReplyId(replyId);
        }

        if(replyId>0)
        {
            ArtistComment artistComment = findById(replyId);

            if(artistComment == null || artistComment.getArtistId()<=0)
            {
                throw new IllegalArgumentException("艺人不正确");
            }
            comment.setArtistId(artistComment.getArtistId());
        }

        comment.setMemberId(memberId);
        comment.setUserName(userName);
        comment.setContent(content);
        comment.setStatus(1);
        comment.setUpdateTime(new Date());
        artistCommentRepository.save(comment);

        //可以考虑发条短信通知。

        return comment.getId();
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

    public List<ArtistComment> findByComment(Integer commentId,int top) {
        List<Object> param = new ArrayList<>();
        if(top >100 || top<=0)
        {
            top =20;
        }
        String condition= getCondition(0,0,"",1,commentId,param);
        String sql = String.format("select * from ArtistComment where 1=1 %s order by id desc limit %d",condition,top);
        return jt.query(sql, param.toArray(), new BeanPropertyRowMapper<>(ArtistComment.class));

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

    public PageResponse<ArtistComment> findByAPI(Integer artistId, Integer memberId,Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="ArtistComment";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getCondition(artistId,memberId,"",1,0,param);

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

        for (ArtistComment comment:ls
             ) {
            //默认回复内容取3条
            comment.setReplyComment(findByComment(comment.getId(),3));
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
        StringBuilder sb = new StringBuilder();
        if(artistId>0)
        {
            sb.append(" AND artistId="+artistId);
        }
        if(memberId>0)
        {
            sb.append(" AND memberId="+memberId);
        }
        if(!StringUtils.isEmpty(username))
        {
            sb.append(" AND username like ?");
            param.add("%+ "+username+" +%");
        }
        if(status>-1)
        {
            sb.append(" AND status="+status);
        }
        if(replyId>0)
        {
            sb.append(" AND replyId="+replyId);
        }
        return sb.toString();
    }

}

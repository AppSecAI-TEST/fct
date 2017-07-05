package com.fct.member.service.business;

import com.fct.core.utils.PageUtil;
import com.fct.member.data.entity.MemberFavourite;
import com.fct.member.data.repository.MemberFavouriteRepository;
import com.fct.member.interfaces.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MemberFavouriteManager {

    @Autowired
    private MemberFavouriteRepository memberFavouriteRepository;

    @Autowired
    private JdbcTemplate jt;

    public void create(Integer memberId,Integer favType,Integer relatedId)
    {
        if(memberId<=0)
        {
            throw  new IllegalArgumentException("会员不存在");
        }
        if(favType<0 || favType>1)
        {
            throw new IllegalArgumentException("收藏类型不正确");
        }
        if(relatedId<0)
        {
            throw new IllegalArgumentException("关联Id为空");
        }
        if(memberFavouriteRepository.getCountByMember(memberId,favType,relatedId)>0)
        {
            throw new IllegalArgumentException("已收藏");
        }
        MemberFavourite favourite = new MemberFavourite();
        favourite.setMemberId(memberId);
        favourite.setFavType(favType);
        favourite.setRelatedId(relatedId);
        favourite.setCreateTime(new Date());
        memberFavouriteRepository.save(favourite);
    }

    public int getCount(Integer memberId,Integer favType,Integer relatedId)
    {
        if(memberId>0) {
            memberFavouriteRepository.getCountByMember(memberId,favType,relatedId);
        }
        return memberFavouriteRepository.getCountByType(favType,relatedId);
    }

    public void delete(Integer memberId,Integer favType,Integer relatedId)
    {
        memberFavouriteRepository.deleteByMember(memberId,favType,relatedId);
    }

    private String getCondition(Integer memberId, Integer favType, List<Object> param)
    {
        String condition ="";
        if(memberId>0)
        {
            condition += " AND memberId="+memberId;
        }
        if(favType>-1)
        {
            condition +=" AND  favtype="+favType;
        }

        return  condition;
    }

    public PageResponse<MemberFavourite> findAll(Integer memberId,Integer favType,Integer pageIndex,Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="MemberFavourite";
        String field ="*";
        String orderBy = "Id asc";
        String condition= getCondition(memberId,favType,param);

        String sql = "SELECT Count(0) FROM MemberFavourite WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<MemberFavourite> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<MemberFavourite>(MemberFavourite.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<MemberFavourite> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;

    }


}

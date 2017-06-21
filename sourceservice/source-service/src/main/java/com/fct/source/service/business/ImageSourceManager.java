package com.fct.source.service.business;

import com.fct.source.data.entity.ImageSource;
import com.fct.source.data.repository.ImageSourceRepository;
import com.fct.source.interfaces.PageResponse;
import com.fct.common.utils.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ImageSourceManager {

    @Autowired
    private ImageSourceRepository imagesSourceRepository;

    @Autowired
    private JdbcTemplate jt;

    public String save(ImageSource imageSource)
    {
        if(StringUtils.isEmpty(imageSource.getGuid())) {

            throw  new IllegalArgumentException("guid 为空");
        }
        imageSource.setCreateTime(new Date());
        imageSource.setSortIndex(0);
        imageSource.setStatus(1);
        imagesSourceRepository.save(imageSource);
        return imageSource.getGuid();
    }

    public ImageSource findById(String guid)
    {
        return imagesSourceRepository.findOne(guid);
    }

    public List<ImageSource> findByGuid(String guids)
    {
        String sql = String.format("SELECT * FROM ImageSource WHERE Status=1 AND guid in (%s)",guids);

        return jt.query(sql,new Object[]{}, new BeanPropertyRowMapper<>(ImageSource.class));
    }

    public void updateStatus(String guid)
    {
        imagesSourceRepository.updateStatus(guid);
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

    public PageResponse<ImageSource> findAll(String name, Integer categoryId, Integer status, String fileType,
                                                     String startTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="ImageSource";
        String field ="*";
        String orderBy = "sortIndex asc";
        String condition= getCondition(name,categoryId,status,fileType,startTime,endTime,param);

        String sql = "SELECT Count(0) FROM ImageSource WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<ImageSource> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<>(ImageSource.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<ImageSource> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;
    }

    public Integer countByCategoryId(Integer cateId)
    {
        return imagesSourceRepository.countByCategoryId(cateId);
    }
}

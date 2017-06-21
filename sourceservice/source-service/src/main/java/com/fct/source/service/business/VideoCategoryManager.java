package com.fct.source.service.business;

import com.fct.source.data.entity.VideoCategory;
import com.fct.source.data.repository.VideoCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class VideoCategoryManager {

    @Autowired
    private VideoCategoryRepository videoCategoryRepository;

    @Autowired
    private VideoSourceManager videoSourceManager;

    @Autowired
    private JdbcTemplate jt;

    //添加商品分类
    public void save (VideoCategory cate) {
        if (StringUtils.isEmpty(cate.getName())) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        int count = videoCategoryRepository.exitSameName(cate.getName(), cate.getId() == null ? 0 : cate.getId());

        if (count > 0) {
            throw new IllegalArgumentException("名称不能重复");
        }
        videoCategoryRepository.save(cate);
    }

    //获取分类列表
    public List<VideoCategory> findAll(String name) {

        String condition = "";
        List<Object> param = new ArrayList<>();
        if (!StringUtils.isEmpty(name)) {
            condition += " AND name like ?";
            param.add("%"+ name +"%");
        }
        String sql = String.format("select * from VideoCategory where 1=1 %s order by sortindex asc",condition);

        List<VideoCategory> list = jt.query(sql,param.toArray(), new BeanPropertyRowMapper<VideoCategory>(VideoCategory.class));
        return list;
    }

    public VideoCategory findById (Integer id)
    {
        if (id < 1) {
            throw new IllegalArgumentException ("ID不存在");
        }
        return videoCategoryRepository.findOne(id);
    }

    public void delete(int id)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("ID不存在");
        }
        //校验产品中是否有使用分类，如有则不可删除
        int exitCount = videoSourceManager.countByCategoryId(id);

        if (exitCount > 0)
        {
            throw new IllegalArgumentException("分类下面有宝贝，不可删除。");
        }

        videoCategoryRepository.delete(id);
    }
}

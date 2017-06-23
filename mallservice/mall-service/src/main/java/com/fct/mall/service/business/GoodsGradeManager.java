package com.fct.mall.service.business;

import com.fct.mall.data.entity.GoodsGrade;
import com.fct.mall.data.repository.GoodsGradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by jon on 2017/5/19.
 */
@Service
public class GoodsGradeManager {

    @Autowired
    private GoodsGradeRepository goodsGradeRepository;

    @Autowired
    private GoodsManager goodsManager;

    @Autowired
    private JdbcTemplate jt;

    //添加商品分类
    public void save (GoodsGrade grade) {
        if (StringUtils.isEmpty(grade.getName())) {
            throw new IllegalArgumentException("品级分类名称不能为空");
        }
        if(StringUtils.isEmpty(grade.getImg()))
        {
            throw new IllegalArgumentException ("图片为空");
        }
        if(grade.getSortIndex() == null)
        {
            throw new IllegalArgumentException ("排序值为空");
        }
        int count = goodsGradeRepository.exitSameName(grade.getName(), grade.getId() == null ? 0 : grade.getId());

        if (count > 0) {
            throw new IllegalArgumentException("名称不能重复");
        }
        goodsGradeRepository.save(grade);
    }

    //获取分类列表
    public List<GoodsGrade> findAll(String name) {

        String condition = "";
        List<Object> param = new ArrayList<>();
        if (!StringUtils.isEmpty(name)) {
            condition += " AND name like ?";
            param.add("%"+ name +"%");
        }
        String sql = String.format("select * from GoodsGrade where 1=1 %s order by sortindex asc",condition);

        List<GoodsGrade> list = jt.query(sql,param.toArray(), new BeanPropertyRowMapper<GoodsGrade>(GoodsGrade.class));
        return list;
    }

    public GoodsGrade findById (Integer id)
    {
        if (id < 1) {
            throw new IllegalArgumentException ("ID不存在");
        }
        return goodsGradeRepository.findOne(id);
    }

    public void saveSortIndex (Integer id, Integer sortIndex)
    {
        if (id < 1) {
            throw new IllegalArgumentException ("ID不存在");
        }
        if(sortIndex ==null)
        {
            throw new IllegalArgumentException ("排序值为空");
        }
        goodsGradeRepository.updateSortIndex(id,sortIndex);

    }

    public void delete(int id)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("ID不存在");
        }
        //校验产品中是否有使用分类，如有则不可删除
        int exitCount = goodsManager.countByGrade(id);

        if (exitCount > 0)
        {
            throw new IllegalArgumentException("品级下面有宝贝，不可删除。");
        }

        goodsGradeRepository.delete(id);
    }
}

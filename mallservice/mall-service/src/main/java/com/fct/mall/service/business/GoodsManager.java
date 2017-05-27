package com.fct.mall.service.business;

import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsSpecification;
import com.fct.mall.data.repository.GoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/16.
 */
@Service
public class GoodsManager {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsSpecificationManager goodsSpecificationManager;

    @Autowired
    private JdbcTemplate jt;

    public Integer countByCategory(Integer categoryId)
    {
        return goodsRepository.countByCategory(categoryId);
    }

    public Integer countByGrade(Integer gradeId)
    {
        return goodsRepository.countByGradeId(gradeId);
    }

    public Goods findById(Integer id)
    {
        return goodsRepository.findOne(id);
    }

    public void save(Goods goods)
    {
        if (StringUtils.isEmpty(goods.getName()))
        {
            throw new IllegalArgumentException("商品名称为空");
        }
        if (StringUtils.isEmpty(goods.getCategoryCode()))
        {
            throw new IllegalArgumentException("请选择商品分类");
        }
        if (goods.getSalePrice().doubleValue() <= 0)
        {
            throw new IllegalArgumentException("售价有误");
        }
        if (goods.getMarketPrice().doubleValue() <= 0)
        {
            throw new IllegalArgumentException("市场价有误");
        }
        if (goods.getStockCount() < 0)
        {
            throw new IllegalArgumentException("库存有误");
        }
        if (StringUtils.isEmpty(goods.getDefaultImage()))
        {
            throw new IllegalArgumentException("商品图片为空");
        }
        if (StringUtils.isEmpty(goods.getMultiImages()))
        {
            throw new IllegalArgumentException("商品图片为空");
        }
        if (goods.getCommission().doubleValue() < 0 ||
                goods.getCommission().doubleValue() > goods.getSalePrice().doubleValue())
        {
            throw new IllegalArgumentException("佣金有误");
        }

        if (StringUtils.isEmpty(goods.getContent()))
        {
            throw new IllegalArgumentException("商品详细为空");
        }
        if(StringUtils.isEmpty(goods.getArtistIds()))
        {
            throw new IllegalArgumentException("合作艺人为空");
        }
        if(goods.getMaterialId()<=0)
        {
            throw new IllegalArgumentException("材质为空");
        }
        if(goods.getGradeId()<=0)
        {
            throw new IllegalArgumentException("品级为空");
        }

        //验证多规格
        if (goods.getSpecification() != null && goods.getSpecification().size() > 0)
        {
            for (GoodsSpecification spec:goods.getSpecification()
                 ) {
                if (StringUtils.isEmpty(spec.getName()))
                {
                    throw new IllegalArgumentException("规格名称为空");
                }
                if (spec.getPrice().doubleValue() <= 0)
                {
                    throw new IllegalArgumentException("规格价格不合法");
                }
                if (spec.getCommission().doubleValue() < 0 || spec.getCommission().doubleValue() < spec.getPrice().doubleValue())
                {
                    throw new IllegalArgumentException("规格佣金不合法");
                }
                if (spec.getStockCount() < 0)
                {
                    throw new IllegalArgumentException("规格库存有误");
                }
            }
        }
        //goods.setCategoryCode("catecode+cateid,");
        goods.setStatus(0);
        goods.setIsDel(0);
        goods.setUpdateTime(new Date());
        if (goods.getId() > 0)
        {
            goodsRepository.saveAndFlush(goods);
        }
        else
        {
            goods.setCreateTime(new Date());
            goodsRepository.save(goods);
        }
        //保存多规格
        if (goods.getSpecification() != null && goods.getSpecification().size() > 0)
        {
            for (GoodsSpecification spec:goods.getSpecification()
                    ) {
                spec.setGoodsId(goods.getId());
                goodsSpecificationManager.save(spec);
            }
        }
    }

    //查询列表 categorycode= catecode+cateid,
    public Page<Goods> find(String name, String categoryCode, Integer gradeId, Integer status,
                            Integer pageIndex, Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<Goods> spec = new Specification<Goods>() {
            @Override
            public Predicate toPredicate(Root<Goods> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(name)) {
                    predicates.add(cb.like(root.get("name"), name));
                }
                if(!StringUtils.isEmpty(categoryCode))
                {
                    predicates.add(cb.equal(root.get("categoryCode"),categoryCode));
                }
                if(status>-1)
                {
                    predicates.add(cb.equal(root.get("status"),status));
                }

                if (gradeId>0) {
                    predicates.add(cb.equal(root.get("gradeId"), gradeId));
                }

                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return goodsRepository.findAll(spec,pageable);

    }

    //修改排序
    public void updateSortIndex(Integer id, Integer sortIndex)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("商品不存在");
        }

        goodsRepository.updateSortIndex(id,sortIndex,new Date().toString());
    }

    //产品上下架 {0:下架,1:上架}
    public void updateStatus(Integer id, Integer status)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("商品不存在");
        }
        goodsRepository.updateStatus(id,status > 0 ? 1 : 0,new Date().toString());
    }

    //添加库存
    public void addStockCount(Integer id, Integer specificationId, Integer stockCount)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("商品不存在");
        }
        if (stockCount < 1)
        {
            throw new IllegalArgumentException("添加库存不能小于1");
        }

        String sql = String.format("UPDATE Goods set StockCount=StockCount+d%, UpdateTime='s%' WHERE Id=d%;",
                stockCount, new Date(), id);

        if (specificationId > 0)
        {
            sql += String.format("UPDATE GoodsSpecification set StockCount=StockCount+d% WHERE Id=d% AND GoodsId=d%",
                    stockCount, specificationId, id);
        }

        jt.update(sql);
    }


    //减库存
    public void subtractStockCount(Integer id, Integer specificationId, Integer stockCount)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("商品不存在");
        }
        if (stockCount < 1)
        {
            throw new IllegalArgumentException("减去的库存不能小于1");
        }

        String sql = String.format("UPDATE Goods set StockCount=StockCount-d%, UpdateTime='s%' WHERE Id=d% AND StockCount>=d%;",
                stockCount, new Date(), id,stockCount);

        if (specificationId > 0)
        {
            sql += String.format("UPDATE GoodsSpecification set StockCount=StockCount-d% WHERE Id=d% AND GoodsId=d% AND StockCount>=d%",
                    stockCount, specificationId, id,stockCount);
        }
        jt.update(sql);

    }

    public void delete(Integer id)
    {
        if (id < 1)
        {
            throw new IllegalArgumentException("商品不存在");
        }

        goodsRepository.delete(new Date().toString(),id);
    }
}

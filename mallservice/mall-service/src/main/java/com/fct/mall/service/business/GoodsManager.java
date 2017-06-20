package com.fct.mall.service.business;

import com.fct.common.utils.DateUtils;
import com.fct.common.utils.PageUtil;
import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsGrade;
import com.fct.mall.data.entity.GoodsSpecification;
import com.fct.mall.data.repository.GoodsRepository;
import com.fct.mall.interfaces.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
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

        return goodsRepository.countByCategory(categoryId +",%");
    }

    public Integer countByGrade(Integer gradeId)
    {
        return goodsRepository.countByGradeId(gradeId);
    }

    public Goods findById(Integer id)
    {

        Goods goods =  goodsRepository.findOne(id);
        List<GoodsSpecification> lsSpec = goodsSpecificationManager.findByGoodsId(id);
        if(lsSpec!=null) {
            goods.setSpecification(lsSpec);
        }
        return goods;
    }

    public List<Goods> findByIds(String ids)
    {
        String sql = "SELECT * FROM Goods Where status=1 AND Id IN("+ ids +")";

        List<Goods> ls = jt.query(sql,new Object[]{},new BeanPropertyRowMapper<Goods>(Goods.class));
        return ls;
    }

    @Transactional
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
        if(StringUtils.isEmpty(goods.getMaterialId()))
        {
            throw new IllegalArgumentException("材质为空");
        }
        if(goods.getMaxVolume() == null && goods.getMaxVolume()<=0)
        {
            throw new IllegalArgumentException("最大容量为空");
        }
        if(goods.getMinVolume() == null && goods.getMinVolume()<=0)
        {
            throw new IllegalArgumentException("最小容量为空");
        }
        if(goods.getMinVolume()>goods.getMaxVolume())
        {
            throw new IllegalArgumentException("最小容量值不能超过最大容量");
        }
        if(goods.getGradeId() == null || goods.getGradeId()<=0)
        {
            throw new IllegalArgumentException("品级为空");
        }

        List<Integer> lsSpecId = new ArrayList<>();
        //验证多规格
        if (goods.getSpecification() != null && goods.getSpecification().size() > 0)
        {
            for (GoodsSpecification spec:goods.getSpecification()) {
                if (StringUtils.isEmpty(spec.getName()))
                {
                    throw new IllegalArgumentException("规格名称为空");
                }
                if (spec.getMarketPrice().doubleValue() <= 0)
                {
                    throw new IllegalArgumentException("规格市场价不合法");
                }
                if (spec.getSalePrice().doubleValue() <= 0)
                {
                    throw new IllegalArgumentException("规格销售价不合法");
                }
                if (spec.getCommission().doubleValue() < 0 || spec.getCommission().doubleValue() > spec.getSalePrice().doubleValue())
                {
                    throw new IllegalArgumentException("规格佣金不合法");
                }
                if (spec.getStockCount() < 0)
                {
                    throw new IllegalArgumentException("规格库存有误");
                }
                if(spec.getId() != null && spec.getId()>0) {
                    lsSpecId.add(spec.getId());
                }
            }
        }
        //goods.setCategoryCode("catecode+cateid,");
        //艺人存入,1,2,10,
        goods.setUpdateTime(new Date());
        Boolean newadd = false;
        if (goods.getId() ==null || goods.getId() == 0)
        {
            newadd = true;
            goods.setIsDel(0);
            goods.setSellCount(0);
            goods.setCommentScore(new Float(5));
            goods.setCommentCount(0);
            goods.setPayCount(0);
            goods.setViewCount(100);

            goods.setCreateTime(new Date());

        }
        goodsRepository.save(goods);

        //移除不必要的规格（隐藏，规避已有交易过的宝贝）。
        if(!newadd)
        {
            goodsSpecificationManager.delete(goods.getId(), lsSpecId);
        }

        //保存多规格
        if (goods.getSpecification() != null && goods.getSpecification().size() > 0)
        {
            for (GoodsSpecification spec:goods.getSpecification()
                    ) {
                spec.setGoodsId(goods.getId());
                spec.setSortIndex(0);
                spec.setIsdel(0);
                goodsSpecificationManager.save(spec);
            }
        }
    }

    public void updateCategory(String newCode,Integer cagetoryId)
    {
        String sql = "update goods set categoryCode='"+ newCode +"' where categoryCode like ',"+ cagetoryId +",%'";

        jt.update(sql);
    }

    //查询列表 categorycode= catecode+cateid,
    public PageResponse<Goods> find(String name, String categoryCode, Integer gradeId,Integer materialId,
                                    Integer artistId,Integer minVolume,Integer maxVolume,Integer status,
                                    Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="Goods";
        String field ="*";
        String orderBy = "sortIndex asc";
        String condition= getContion(name,categoryCode,gradeId,materialId,artistId,minVolume,maxVolume,status,param);

        String sql = "SELECT Count(0) FROM Goods WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<Goods> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<Goods>(Goods.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<Goods> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;
    }

    private String getContion(String name, String categoryCode, Integer gradeId, Integer materialId,
                              Integer artistId,Integer minVolume,Integer maxVolume,
                              Integer status,List<Object> param)
    {
        String condition=" AND isDel=0 ";
        if (!StringUtils.isEmpty(name)) {
            condition += " AND name like ?";
            param.add("%"+ name +"%");
        }
        if(!StringUtils.isEmpty(categoryCode))
        {
            condition += " AND categoryCode like ?";
            param.add(categoryCode+"%");
        }
        if(status>-1)
        {
            condition += " AND status="+status;
        }

        if (gradeId>0) {
            condition += " AND gradeId="+gradeId;
        }
        if (materialId>0) {
            condition += " AND materialId like ?";
            param.add(","+ materialId +",");
        }
        if (minVolume>0) {
            condition += " AND minVolume="+minVolume;
        }
        if (maxVolume>0) {
            condition += " AND maxVolume="+maxVolume;
        }
        if (artistId>0) {
            condition += " AND artistId like ?";
            param.add(","+artistId +",");
        }
        return condition;
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

        String sql = String.format("UPDATE Goods set StockCount=StockCount+%d, UpdateTime='%s' WHERE Id=d%;",
                stockCount, new Date(), id);

        if (specificationId > 0)
        {
            sql += String.format("UPDATE GoodsSpecification set StockCount=StockCount+%d WHERE Id=%d AND GoodsId=%d",
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

        String sql = String.format("UPDATE Goods set StockCount=StockCount-%d, UpdateTime='%s' WHERE Id=%d AND StockCount>=%d;",
                stockCount, new Date(), id,stockCount);

        if (specificationId > 0)
        {
            sql += String.format("UPDATE GoodsSpecification set StockCount=StockCount-%d WHERE Id=%d AND GoodsId=%d AND StockCount>=%d",
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

        Goods g = goodsRepository.findOne(id);
        g.setIsDel(1);
        g.setUpdateTime(new Date());
        goodsRepository.save(g);
    }
}

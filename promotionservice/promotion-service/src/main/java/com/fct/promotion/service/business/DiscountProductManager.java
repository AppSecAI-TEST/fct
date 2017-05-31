package com.fct.promotion.service.business;

import com.fct.common.exceptions.BaseException;
import com.fct.promotion.data.entity.Discount;
import com.fct.promotion.data.entity.DiscountProduct;
import com.fct.promotion.data.repository.DiscountProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/13.
 */
@Service
public class DiscountProductManager {

    @Autowired
    DiscountProductRepository discountProductRepository;

    @Autowired
    JdbcTemplate jt;

    private DiscountProduct save(DiscountProduct obj)
    {
        obj.setLastUpdateTime(new Date());
        if (obj.getId() > 0)
        {
            discountProductRepository.saveAndFlush(obj);
        }
        else
        {
            obj.setCreateTime(new Date());
            discountProductRepository.save(obj);
        }
        return obj;
    }

    public void add(Discount discount, List<DiscountProduct> lst)
    {
        checkValid(discount, lst);
        for (DiscountProduct obj:lst
             ) {
            save(obj);
        }
    }

    public void deleteByDiscountId(Integer discountId)
    {
        String query = "insert into DiscountProductHistory select Id,DiscountId,ProductId,DiscountRate,SingleCount,IsValidForSize,CreateUserId,CreateTime,LastUpdateUserId,LastUpdateTime,GETDATE() from DiscountProduct where DiscountId="+discountId;
        jt.execute(query);

        query = "delete from DiscountProduct where DiscountId="+discountId;
        jt.execute(query);
    }

    public DiscountProduct findById(Integer id)
    {
        return discountProductRepository.findOne(id);
    }

    public List<DiscountProduct> findByDiscountId(Integer discountId)
    {
        return discountProductRepository.findByDiscountId(discountId);
    }

    public List<DiscountProduct> findByValid(List<Integer> productIds, Integer filterNoBegin)
    {
        String ids = "";

        for (Integer id:productIds
             ) {
            ids += id + ",";
        }
        ids = ids.substring(ids.length()-1);

        String sql = String.format("select p.* from discount d inner join DiscountProduct p  on d.Id = p.DiscountId where d.AuditStatus=1 and p.ProductId in (" + ids + ") and d.EndTime>='%s'",new Date());
        if (filterNoBegin == 1)
        {
            sql += " AND (d.StartTime<='" + new Date() + "' OR d.NotStartCanNotBuy=1)";
        }
        return jt.queryForList(sql,DiscountProduct.class);
    }


    void checkValid(Discount discount, List<DiscountProduct> lst)
    {
        for (DiscountProduct obj:lst
             ) {
            checkValid(discount,obj);
        }
    }

    private void checkValid(Discount discount, DiscountProduct obj)
    {
        if (obj.getProductId() < 1)
        {
            throw new BaseException("产品不能为空");
        }
        if (obj.getDiscountRate().doubleValue() > 1 || obj.getDiscountRate().doubleValue() <= 0)
        {
            throw new BaseException("请输入正确的折扣信息");
        }
        if (obj.getSingleCount() < 1)
        {
            throw new BaseException("请输入正确的限购数量");
        }
        if (hasConflict(discount, obj))
        {
            throw new BaseException("该商品存在冲突的促销：" + obj.getProductId());
        }
    }

    private Boolean hasConflict(Discount discount, DiscountProduct obj)
    {
        //检查有无冲突的产品促销
        String query = "select count(1) from Discount d inner join DiscountProduct p on d.Id=p.DiscountId where ((startTime >= ?1 AND startTime < ?2) OR (startTime < ?1 AND endTime > ?2) OR (endTime > ?1 AND endTime <= ?2)) and p.ProductId=?3";
        if (discount.getId() > 0)
        {
            query += " and d.Id!="+discount.getId();
        }

        Integer exeCount  =jt.queryForObject(query,new Object[] {discount.getStartTime(),discount.getEndTime(),obj.getProductId()},Integer.class);

        return  exeCount>0;
    }
}

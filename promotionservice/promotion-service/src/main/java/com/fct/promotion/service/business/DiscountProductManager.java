package com.fct.promotion.service.business;

import com.fct.core.utils.DateUtils;
import com.fct.promotion.data.entity.Discount;
import com.fct.promotion.data.entity.DiscountProduct;
import com.fct.promotion.data.repository.DiscountProductRepository;
import com.fct.promotion.interfaces.dto.DiscountProductDTO;
import com.fct.promotion.interfaces.dto.OrderProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/13.
 */
@Service
public class DiscountProductManager {

    @Autowired
    private DiscountProductRepository discountProductRepository;

    @Autowired
    private JdbcTemplate jt;

    public DiscountProduct save(DiscountProduct obj)
    {
        obj.setLastUpdateTime(new Date());
        if (obj.getId()== null || obj.getId() ==  0)
        {
            obj.setCreateTime(new Date());
            obj.setIsValidForSize(false);
        }
        discountProductRepository.save(obj);
        return obj;
    }

    public void add(Discount discount, List<DiscountProduct> lst)
    {
        //checkValid(discount, lst);
        for (DiscountProduct obj:lst
             ) {
            save(obj);
        }
    }

    @Transactional
    public void deleteByDiscountId(Integer discountId)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into DiscountProductHistory(id,DiscountId,ProductId,DiscountRate," +
                "SingleCount,IsValidForSize,CreateUserId,CreateTime,LastUpdateUserId,LastUpdateTime,deleteTime)" +
                " select Id,DiscountId,ProductId,DiscountRate,SingleCount,IsValidForSize," +
                "CreateUserId,CreateTime,LastUpdateUserId,LastUpdateTime,now()" +
                " from DiscountProduct where DiscountId="+discountId);

        jt.execute(sb.toString());

        String query = "delete from DiscountProduct where DiscountId="+discountId;

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

        if(productIds == null || productIds.size() <=0)
        {
            throw new IllegalArgumentException("宝贝为空");
        }

        for (Integer id:productIds
             ) {
            if(!StringUtils.isEmpty(ids))
            {
                ids += ",";
            }
            ids += id;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("select p.* from Discount d inner join DiscountProduct p  on d.Id = p.DiscountId where d.AuditStatus=1 and p.ProductId in (" + ids + ") and d.EndTime>='%s'",
                DateUtils.format(new Date())));
        if (filterNoBegin == 1)
        {
            sb.append(" AND (d.StartTime<='" + DateUtils.getNowDateStr("yyyy-MM-dd HH:mm") + "' OR d.NotStartCanNotBuy=1)");
        }
        return jt.query(sb.toString(), new Object[]{}, new BeanPropertyRowMapper<DiscountProduct>(DiscountProduct.class));
        //return jt.queryForList(sql,DiscountProduct.class);
    }


    public void checkValid(Discount discount, List<DiscountProduct> lst)
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
            throw new IllegalArgumentException("产品不能为空");
        }
        if (obj.getDiscountRate().doubleValue() > 1 || obj.getDiscountRate().doubleValue() <= 0)
        {
            throw new IllegalArgumentException("请输入正确的折扣信息");
        }
        if (obj.getSingleCount() < 0)
        {
            throw new IllegalArgumentException("请输入正确的限购数量");
        }
        if (hasConflict(discount, obj))
        {
            throw new IllegalArgumentException("该商品存在冲突的促销：" + obj.getProductId());
        }

    }

    private Boolean hasConflict(Discount discount, DiscountProduct obj)
    {
        //检查有无冲突的产品促销
        String starttime = DateUtils.format(discount.getStartTime());
        String endtime =DateUtils.format(discount.getEndTime());

        StringBuilder sb = new StringBuilder();
        sb.append("select count(0) from Discount d inner join DiscountProduct p on d.Id=p.DiscountId where ");
        sb.append(String.format("((d.startTime >= '%s' AND d.startTime < '%s') ",starttime,endtime));
        sb.append(String.format(" OR (d.startTime < '%s' AND d.endTime > '%s') ",starttime,endtime));
        sb.append(String.format(" OR (d.endTime > '%s' AND d.endTime <= '%s'))",starttime,endtime));
        sb.append(" and p.ProductId="+obj.getProductId());

        if (discount.getId() > 0)
        {
            sb.append(" and d.Id!="+discount.getId());
        }

        Integer exeCount  =jt.queryForObject(sb.toString(),Integer.class);

        return  exeCount>0;
    }
}

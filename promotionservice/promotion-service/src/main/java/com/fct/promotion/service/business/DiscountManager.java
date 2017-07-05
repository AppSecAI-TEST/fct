package com.fct.promotion.service.business;

import com.fct.core.json.JsonConverter;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.PageUtil;
import com.fct.promotion.data.entity.CouponOperateLog;
import com.fct.promotion.data.entity.Discount;
import com.fct.promotion.data.entity.DiscountProduct;
import com.fct.promotion.data.repository.DiscountRepository;
import com.fct.promotion.interfaces.PageResponse;
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
 * Created by jon on 2017/5/13.
 * Give Nancy happiness
 */
@Service
public class DiscountManager {
    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private DiscountProductManager discountProductManager;

    @Autowired
    private CouponOperateLogManager couponOperateLogManager;

    @Autowired
    private JdbcTemplate jt;

    public Discount findById(int id)
    {
        return discountRepository.findOne(id);
    }

    public void add(Discount discount)
    {
        List<DiscountProduct> lst = discount.getProductList();
        checkValid(discount, null);
        discountProductManager.checkValid(discount,lst);
        discount.setProductCount(lst.size()); //更新折扣宝贝数量

        discount.setLastUpdateTime(new Date());
        discount.setCreateTime(new Date());
        discount.setLastUpdateUserId(discount.getCreateUserId());
        discount.setAuditStatus(1);//默认审核通过

        discountRepository.save(discount);

        for (DiscountProduct p:lst
             ) {
            p.setDiscountId(discount.getId());
            p.setCreateUserId(discount.getCreateUserId());
            p.setLastUpdateUserId(discount.getCreateUserId());
            p.setLastUpdateTime(new Date());
            p.setCreateTime(new Date());

            discountProductManager.save(p);
        }
    }

    public List<Discount> findByDiscountId(List<Integer> discountIds)
    {
        String ids = "";
        for (Integer id:discountIds
             ) {
            ids += id + ",";
        }

        ids = ids.substring(ids.length()-1);

        String sql = "select * from Discount where id in (" + ids + ")";
        return jt.query(sql, new Object[]{}, new BeanPropertyRowMapper<Discount>(Discount.class));
    }

    public void update(Discount obj)
    {
        List<DiscountProduct> lst = obj.getProductList();
        Discount oldPromotion = findById(obj.getId());
        checkValid(obj, oldPromotion);

        discountProductManager.checkValid(obj,lst);

        if (oldPromotion.getAuditStatus() == 1)
        {
            obj.setAuditStatus(oldPromotion.getAuditStatus());
            obj.setCreateUserId(oldPromotion.getCreateUserId());
            obj.setCreateTime(oldPromotion.getCreateTime());
        }

        obj.setProductCount(lst.size());   //更新折扣宝贝数量

        obj.setLastUpdateTime(new Date());
        discountRepository.save(obj);

        discountProductManager.deleteByDiscountId(obj.getId());

        for (DiscountProduct p:lst
                ) {
            p.setDiscountId(obj.getId());
            p.setCreateUserId(obj.getCreateUserId());
            p.setLastUpdateUserId(obj.getCreateUserId());
            p.setLastUpdateTime(new Date());
            p.setCreateTime(new Date());
            discountProductManager.save(p);
        }

        //记录操作日志
        CouponOperateLog log = new CouponOperateLog();
        log.setTypeName("Discount");
        log.setOperateId(obj.getLastUpdateUserId());
        log.setOperateTime(new Date());
        log.setRelationId(obj.getId().toString());
        log.setOldContent(JsonConverter.toJson(oldPromotion));
        log.setNewContent(JsonConverter.toJson(obj));
        couponOperateLogManager.add(log);
    }

    public void audit(Integer discountId, Boolean pass, Integer userId)
    {
        Discount obj = findById(discountId);
        if (obj.getAuditStatus() == 1)
        {
            throw new IllegalArgumentException("促销状态已是最终状态，不能修改");
        }
        obj.setAuditStatus(pass ? 1 : 2);
        obj.setLastUpdateUserId(userId);
        obj.setLastUpdateTime(new Date());
        discountRepository.save(obj);
    }

    private String getCondition(String name,String goodsName,Integer status, String startTime, String endTime,List<Object> param)
    {
        String condition ="";
        if(status>-1)
        {
            condition += " AND AuditStatus="+status;
        }
        if(!StringUtils.isEmpty(goodsName))
        {
            condition +=" AND Id IN(select discountid from DiscountProduct where productname like ?)";
            param.add("%"+ goodsName +"%");
        }
        if(!StringUtils.isEmpty(name))
        {
            condition +=" AND name like ?";
            param.add("%"+ name +"%");
        }

        if (!StringUtils.isEmpty(startTime)) {
            condition += " AND startTime>=?";
            param.add(startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            condition += " AND endTime <?";
            param.add(endTime);
        }
        return condition;
    }

    public PageResponse<Discount> findAll(String name,String goodsName,Integer status, String startTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="Discount";
        String field ="*";
        String orderBy = "Id Desc";
        String condition= getCondition(name,goodsName,status,startTime,endTime,param);

        String sql = "SELECT Count(0) FROM Discount WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<Discount> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<Discount>(Discount.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }

        PageResponse<Discount> pageResponse = new PageResponse<>();
        pageResponse.setTotalCount(count);
        pageResponse.setCurrent(end);
        pageResponse.setElements(ls);
        pageResponse.setHasMore(hasmore);

        return pageResponse;

    }

    private void checkValid(Discount obj, Discount oldDiscount)
    {
        if (obj == null)
        {
            throw new IllegalArgumentException("优惠券策略对象不能为null");
        }

        if (DateUtils.compareDate(obj.getStartTime(),obj.getEndTime())>=0)
        {
            throw new IllegalArgumentException("促销开始时间不能大于结束时间");
        }

        if (obj.getId() > 0 && obj.getAuditStatus() == 1)
        {
            if (oldDiscount == null)
            {
                oldDiscount = findById(obj.getId());
                if (oldDiscount == null)
                {
                    throw new IllegalArgumentException("原对象不存在");
                }
            }

            if (DateUtils.compareDate(obj.getEndTime(),new Date())<0)
            {
                throw new IllegalArgumentException("促销已结束，不能修改");
            }

            if (DateUtils.compareDate(obj.getStartTime(),oldDiscount.getStartTime())>0)
            {
                throw new IllegalArgumentException("开始时间不能延后");
            }
            if (DateUtils.compareDate(obj.getEndTime(),oldDiscount.getEndTime())<0)
            {
                throw new IllegalArgumentException("结束时间不能提前结束");
            }
        }
    }
}

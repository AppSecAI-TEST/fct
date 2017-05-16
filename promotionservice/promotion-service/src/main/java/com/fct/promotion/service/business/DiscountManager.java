package com.fct.promotion.service.business;

import com.fct.common.exceptions.BaseException;
import com.fct.common.json.JsonConverter;
import com.fct.common.utils.DateUtils;
import com.fct.promotion.data.entity.CouponOperateLog;
import com.fct.promotion.data.entity.Discount;
import com.fct.promotion.data.entity.DiscountProduct;
import com.fct.promotion.data.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
    DiscountRepository discountRepository;

    @Autowired
    DiscountProductManager discountProductManager;

    @Autowired
    CouponOperateLogManager couponOperateLogManager;

    @Autowired
    JdbcTemplate jt;

    private Discount save(Discount obj)
    {
        obj.setLastUpdateTime(new Date());
        if (obj.getId() > 0)
        {
            discountRepository.saveAndFlush(obj);
        }
        else
        {
            obj.setCreateTime(new Date());
            obj.setAuditStatus(1);//默认审核通过
            discountRepository.save(obj);
        }
        return obj;
    }

    public Discount findById(int id)
    {
        return discountRepository.findOne(id);
    }

    public void add(Discount obj, List<DiscountProduct> lst)
    {
        checkValid(obj, null);
        discountProductManager.checkValid(obj,lst);
        obj.setProductCount(lst.size()); //更新折扣宝贝数量

        //以后事务操作
        save(obj);

        for (DiscountProduct p:lst
             ) {
            p.setDiscountId(obj.getId());
            p.setCreateUserId(obj.getCreateUserId());
            p.setLastUpdateUserId(obj.getCreateUserId());
            p.setLastUpdateTime(new Date());
            p.setCreateTime(new Date());
        }
        discountProductManager.add(obj,lst);
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
        return jt.queryForList(sql,Discount.class);
    }

    public void update(Discount obj, List<DiscountProduct> lst)
    {
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
        save(obj);

        for (DiscountProduct p:lst
                ) {
            p.setDiscountId(obj.getId());
            p.setCreateUserId(obj.getCreateUserId());
            p.setLastUpdateUserId(obj.getCreateUserId());
            p.setLastUpdateTime(new Date());
            p.setCreateTime(new Date());
        }
        discountProductManager.deleteByDiscountId(obj.getId());
        discountProductManager.add(obj,lst);


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
            throw new BaseException("促销状态已是最终状态，不能修改");
        }
        obj.setAuditStatus(pass ? 1 : 2);
        obj.setLastUpdateUserId(userId);
        this.save(obj);
    }

    public Page<Discount> findAll(Integer status,String startTime,String endTime,Integer pageIndex,Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<Discount> spec = new Specification<Discount>() {
            @Override
            public Predicate toPredicate(Root<Discount> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                java.util.List<Predicate> predicates = new ArrayList<Predicate>();

                if(status>0)
                {
                    predicates.add(cb.equal(root.get("Status"),status));
                }
                if (!org.apache.commons.lang3.StringUtils.isEmpty(startTime)) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), startTime));
                }
                if (!org.apache.commons.lang3.StringUtils.isEmpty(endTime)) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endTime));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return discountRepository.findAll(spec,pageable);

    }

    private void checkValid(Discount obj, Discount oldDiscount)
    {
        if (obj == null)
        {
            throw new IllegalArgumentException("优惠券策略对象不能为null");
        }

        if (DateUtils.compareDate(obj.getStartTime(),obj.getEndTime())>=0)
        {
            throw new BaseException("促销开始时间不能大于结束时间");
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
                throw new BaseException("促销已结束，不能修改");
            }

            if (DateUtils.compareDate(obj.getStartTime(),oldDiscount.getStartTime())>0)
            {
                throw new BaseException("开始时间不能延后");
            }
            if (DateUtils.compareDate(obj.getEndTime(),oldDiscount.getEndTime())<0)
            {
                throw new BaseException("结束时间不能提前结束");
            }
        }
    }

    private Boolean hasConflict(Discount obj)
    {
        //检查有无冲突的促销
        String sql = "select count(0) from Discount where ((startTime >= '"+ obj.getStartTime() +"' AND ";
        sql += "startTime < '"+ obj.getEndTime() +"') OR (startTime < '"+ obj.getStartTime() +"' AND endTime > '";
        sql += obj.getEndTime() +"') OR (endTime > '"+ obj.getStartTime() +"' AND endTime <= '"+ obj.getEndTime() +"'))";
        if (obj.getId() > 0)
        {
            sql += " and Id!=" + obj.getId();
        }
        return jt.queryForObject(sql,Integer.class)<0;
    }
}

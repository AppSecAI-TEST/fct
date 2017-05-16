package com.fct.promotion.service.business;

import com.fct.common.exceptions.BaseException;
import com.fct.common.json.JsonConverter;
import com.fct.common.utils.DateUtils;
import com.fct.promotion.data.entity.CouponOperateLog;
import com.fct.promotion.data.entity.CouponPolicy;
import com.fct.promotion.data.repository.CouponPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
 * Created by jon on 2017/5/9.
 */
@Service
public class CouponPolicyManager {

    @Autowired
    CouponPolicyRepository couponPolicyRepository;

    @Autowired
    CouponOperateLogManager couponOperateLogManager;

    private CouponPolicy save(CouponPolicy policy)
    {
        policy.setLastUpdateTime(new Date());
        if (policy.getId() > 0)
        {
            couponPolicyRepository.saveAndFlush(policy);
        }
        else
        {
            policy.setCreateTime(new Date());
            policy.setAuditStatus(1); //默认审核通过
            couponPolicyRepository.save(policy);
        }
        return policy;
    }

    public CouponPolicy findById(Integer policyId)
    {
        if (policyId == 0)
        {
            return null;
        }
        return couponPolicyRepository.findOne(policyId);
    }

    public CouponPolicy add(CouponPolicy policy)
    {
        checkValid(policy, null);
        if (!StringUtils.isEmpty(policy.getProductIds()))
        {
            policy.setTypeId(1);
        }
        return save(policy);
    }

    public CouponPolicy update(CouponPolicy policy)
    {
        CouponPolicy oldPolicy = findById(policy.getId());
        checkValid(policy, oldPolicy);
        policy.setCreateUserId(oldPolicy.getCreateUserId());
        policy.setCreateTime(oldPolicy.getCreateTime());
        if (oldPolicy.getAuditStatus() == 1)
        {
            policy.setTypeId(oldPolicy.getTypeId());
            policy.setAmount(oldPolicy.getAmount());
            policy.setUsingType(oldPolicy.getUsingType());
            policy.setFullAmount(oldPolicy.getFullAmount());
            policy.setAuditStatus(oldPolicy.getAuditStatus());
            policy.setFetchType(oldPolicy.getFetchType());
            policy.setReceivedCount(oldPolicy.getReceivedCount());
        }

        if (policy.getFetchType() == 1 && policy.getTotalCount() > oldPolicy.getTotalCount())
        {
            policy.setGenerateStatus(0);
        }

        //记录操作日志
        CouponOperateLog log = new CouponOperateLog();
        log.setTypeName("Coupon");
        log.setOperateId(policy.getLastUpdateUserId());
        log.setOperateTime(new Date());
        log.setRelationId(policy.getId().toString());
        log.setOldContent(JsonConverter.toJson(oldPolicy));
        log.setNewContent(JsonConverter.toJson(policy));
        couponOperateLogManager.add(log);

        return save(policy);
    }

    public void audit(Integer policyId, Boolean pass, Integer userId)
    {
        CouponPolicy policy = findById(policyId);
        if (policy.getAuditStatus() == 1)
        {
            throw new BaseException("优惠券状态已是最终状态，不能修改");
        }
        policy.setAuditStatus(pass ? 1 : 2);
        policy.setLastUpdateUserId(userId);
        this.save(policy);
    }

    public Page<CouponPolicy> findAll(Integer status,Integer fetchType,Integer generateStatus, String startTime,
                                      String endTime,Integer pageIndex, Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<CouponPolicy> spec = new Specification<CouponPolicy>() {
            @Override
            public Predicate toPredicate(Root<CouponPolicy> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                java.util.List<Predicate> predicates = new ArrayList<Predicate>();

                if(status>0)
                {
                    predicates.add(cb.equal(root.get("AuditStatus"),status));
                }
                if(fetchType>0)
                {
                    predicates.add(cb.equal(root.get("FetchType"),fetchType));
                }
                if(generateStatus>0)
                {
                    predicates.add(cb.equal(root.get("GenerateStatus"),generateStatus));
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

        return couponPolicyRepository.findAll(spec,pageable);

    }

    public List<CouponPolicy> findByCanReceive()
    {
        return couponPolicyRepository.findByCanReceive(new Date().toString());
    }

    void addReceiveCount(Integer policyId)
    {
        couponPolicyRepository.addReceiveCount(policyId);
    }


    void updateGenerateStatus(Integer policyId)
    {
        couponPolicyRepository.updateGenerateStatus(policyId);
    }

    List<CouponPolicy> findNeedGenerate()
    {
        return couponPolicyRepository.findNeedGenerate();
    }

    private void checkValid(CouponPolicy policy, CouponPolicy oldPolicy)
    {
        if (policy == null)
        {
            throw new IllegalArgumentException("优惠券策略对象不能为null");
        }

        //面额
        if (policy.getAmount().doubleValue() <= 0)
        {
            throw new BaseException("优惠券面额不合法");
        }
        if (policy.getUsingType() == 1)
        {
            if (policy.getFullAmount().doubleValue() <= 0)
            {
                throw new BaseException("优惠券满XX不合法");
            }
            if (policy.getAmount().doubleValue() > policy.getFullAmount().doubleValue())
            {
                throw new BaseException("优惠券面额不能大于满额");
            }
        }
        if (DateUtils.compareDate(policy.getStartTime(),policy.getEndTime())>=0)
        {
            throw new BaseException("优惠券开始时间不能大于结束时间");
        }
        if (policy.getTotalCount() < 1 || policy.getSingleCount() < 1)
        {
            throw new BaseException("优惠券总发行量/每人领取券不合法");
        }

        if (policy.getTypeId() == 1 && StringUtils.isEmpty(policy.getProductIds()))
        {
            throw new BaseException("请选择享受优惠的商品");
        }

        if (!StringUtils.isEmpty(policy.getProductIds()))
        {
            String[] arr = policy.getProductIds().split(",");
            if (arr.length > 100)
            {
                throw new BaseException("商品数量不能大于100");
            }
        }

        if (policy.getId() > 0 && policy.getAuditStatus() == 1)
        {
            if (oldPolicy == null)
            {
                oldPolicy = findById(policy.getId());
                if (oldPolicy == null)
                {
                    throw new IllegalArgumentException("原对象不存在");
                }
            }

            if ( DateUtils.compareDate(policy.getEndTime(),new Date())<0)
            {
                throw new BaseException("优惠券已结束，不能修改");
            }

            if (DateUtils.compareDate(policy.getStartTime(),oldPolicy.getStartTime())>0)
            {
                throw new BaseException("开始时间不能延后");
            }
            if (DateUtils.compareDate(policy.getEndTime(),oldPolicy.getEndTime())<0)
            {
                throw new BaseException("结束时间不能提前结束");
            }
            if (DateUtils.compareDate(policy.getEndTime(),oldPolicy.getEndTime())>0)
            {
                //已过期的优惠券无法再重新激活
                throw new BaseException("结束时间不能延后");
            }

            if (policy.getTotalCount() < oldPolicy.getTotalCount())
            {
                throw new BaseException("发行量不能减少");
            }
            if (policy.getSingleCount() < oldPolicy.getSingleCount())
            {
                throw new BaseException("每人领取量不能减少");
            }

            if (!StringUtils.isEmpty(oldPolicy.getProductIds()))
            {
                String[] arr = oldPolicy.getProductIds().split(",");
                String temp = String.format(",s%,", policy.getProductIds());
                for (String productId:arr
                     ) {
                    if (!temp.contains("," + productId + ","))
                    {
                        throw new BaseException("不能删除原享受优惠的产品：" + productId);
                    }
                }
            }
        }
    }
}

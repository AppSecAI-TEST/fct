package com.fct.promotion.service.business;

import com.fct.core.utils.DateUtils;
import com.fct.promotion.data.entity.CouponCode;
import com.fct.promotion.data.entity.CouponPolicy;
import com.fct.promotion.data.repository.CouponCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/12.
 */
@Service
public class CouponCodeManager {

    @Autowired
    private CouponCodeRepository couponCodeRepository;

    @Autowired
    private CouponPolicyManager couponPolicyManager;

    @Autowired
    private CouponSpareCodeManager couponSpareCodeManager;

    @Autowired
    private JdbcTemplate jt;

    static Object syncObj = new Object();

    @Transactional
    public String receive(Integer memberId, Integer policyId) {
        if (memberId <= 0) {
            throw new IllegalArgumentException("会员不存在");
        }
        if (policyId <= 0) {
            throw new IllegalArgumentException("优惠券id不存在");
        }
        CouponPolicy policy = couponPolicyManager.findById(policyId);
        if (policy == null) {
            throw new IllegalArgumentException("该优惠券不存在");
        }
        if (DateUtils.compareDate(new Date(), policy.getEndTime()) > 0 || policy.getAuditStatus() != 1) {
            throw new IllegalArgumentException("优惠券活动已过期");
        }

        if (policy.getFetchType() != 0) {
            throw new IllegalArgumentException("该优惠券不支持领取");
        }
        String code = "";
        synchronized (syncObj) {
            int totalSendCount = getSendCount(policyId, 0);
            if (totalSendCount >= policy.getTotalCount()) {
                throw new IllegalArgumentException("优惠券已发放完");
            }
            int memberSendCount = this.getSendCount(policyId, memberId);
            if (memberSendCount >= policy.getSingleCount()) {
                throw new IllegalArgumentException("您已领用该优惠券");
            }
            code = couponSpareCodeManager.getValidCode();
            CouponCode obj = new CouponCode();
            obj.setPolicyId(policyId);
            obj.setStatus(0);
            obj.setMemberId(memberId);
            obj.setCode(code);
            obj.setCreateTime(new Date());
            this.save(obj);

            //更新数量
            couponPolicyManager.addReceiveCount(policyId);
        }
        couponSpareCodeManager.setCodeUsed(code);
        return code;
    }

    public void setCodeUsing(Integer policyId,String code) {

        String sql = String.format("update CouponCode set Status=1,UseTime='%s',LastUpdateTime='%s' where policyId=%d and code='%s' and Status=0",
                DateUtils.format(new Date()),DateUtils.format(new Date()),policyId,code);
        int count = jt.update(sql);
        if (count < 1) {
            throw new IllegalArgumentException("优惠券使用出错");
        }
    }

    public void setCodeUsed(String code) {
        String sql = String.format("update CouponCode set Status =2,LastUpdateTime='%s' where code='%s' and Status=1",
                DateUtils.format(new Date()),code);

        int count = jt.update(sql);
        if (count < 1) {
            throw new IllegalArgumentException("优惠券使用成功出错");
        }
    }

    public void cancelCodeUsed(String code) {
        String sql = String.format("update CouponCode set Status =0,LastUpdateTime='%s' where code='%s' and Status!=0",
                DateUtils.format(new Date()),code);
        int count = jt.update(sql);
        if (count < 1) {
            throw new IllegalArgumentException("优惠券取消使用成功出错");
        }
    }

    /// <summary>
    /// 获取用户已经领满的优惠券Id列表
    /// </summary>
    /// <param name="memberId"></param>
    /// <param name="policyIds"></param>
    /// <returns></returns>
    public List<Integer> findReceivedPolicyId(Integer memberId, List<Integer> policyIds) {
        String ids = "";

        for (Integer id : policyIds
                ) {
            ids += id + ",";
        }
        ids = ids.substring(ids.length() - 1);

        String sql = "select t.PolicyId from(select PolicyId,COUNT(0) as num from CouponCode   where memberid=" + memberId + "   and PolicyId in (" + ids + ")   group by PolicyId) t inner join CouponPolicy p  on t.PolicyId = p.Id  where t.num>=p.SingleCount";

        return jt.queryForList(sql, Integer.class);

    }


    public void receiveSystemCouponCode(Integer memberId, CouponPolicy policy,CouponCode coupon) {

        if (memberId <= 0) {
            throw new IllegalArgumentException("会员不存在");
        }

        int memberSendCount = this.getSendCount(policy.getId(), memberId);
        if (memberSendCount >= policy.getSingleCount()) {
            throw new IllegalArgumentException("该优惠活动您已领用过，超过次数限制");
        }

        if (coupon.getMemberId() > 0) {
            throw new IllegalArgumentException("优惠券异常，重复领取!!!");
        }

        //更新数量
        coupon.setStatus(0);
        coupon.setMemberId(memberId);
        couponPolicyManager.addReceiveCount(policy.getId());
    }

    public CouponCode findByCode(String code) {
        if (StringUtils.isEmpty(code)) {
            throw new IllegalArgumentException("优惠券码为空");
        }
        return couponCodeRepository.findByCode(code);
    }

    String receiveForSystem(Integer policyId) {
        if (policyId <= 0) {
            throw new IllegalArgumentException("优惠券id不存在");
        }
        CouponPolicy policy = couponPolicyManager.findById(policyId);
        if (policy == null) {
            throw new IllegalArgumentException("该优惠券不存在");
        }

        if (policy.getFetchType() != 1) {
            throw new IllegalArgumentException("该优惠券不支持系统发放");
        }

        String code = "";
        synchronized (syncObj) {
            Integer totalSendCount = this.getSendCount(policyId, 0);
            if (totalSendCount >= policy.getTotalCount()) {
                throw new IllegalArgumentException("优惠券已发放完");
            }
            code = couponSpareCodeManager.getValidCode();
            CouponCode obj = new CouponCode();
            obj.setPolicyId(policyId);
            obj.setStatus(0);
            obj.setCode(code);
            obj.setCreateTime(new Date());
            obj.setLastUpdateTime(new Date());
            obj.setMemberId(0);
            this.save(obj);

        }
        couponSpareCodeManager.setCodeUsed(code);
        return code;
    }

    public CouponCode save(CouponCode obj) {
        obj.setLastUpdateTime(new Date());
        if (obj.getId() == null || obj.getId() <= 0) {
            obj.setCreateTime(new Date());
        }
        couponCodeRepository.save(obj);
        return obj;
    }


    public Integer getSendCount(Integer policyId, Integer memberId) {
        String sql = "select count(0) from CouponCode where policyId=" + policyId;
        if (memberId > 0) {
            sql += " and MemberId=" + memberId;
        }
        return jt.queryForObject(sql, Integer.class);
    }

    public void setStatusExpire() {
        String sql = "update CouponCode set Status = 2,LastUpdateTime = ?  where Status =0 and Id in (select top 10000 c.Id from CouponCode c inner join CouponPolicy p on c.PolicyId = p.Id where c.Status =0  and p.EndTime<? and p.EndTime> ?";
        List<Object> param = new ArrayList<>();
        param.add(DateUtils.format(new Date()));
        param.add(DateUtils.format(new Date()));
        param.add(DateUtils.format(DateUtils.addDay(new Date(),-3)));
        jt.update(sql,param);
    }
}

package com.fct.promotion.service;

import com.fct.promotion.data.entity.CouponPolicy;
import com.fct.promotion.data.entity.Discount;
import com.fct.promotion.data.entity.DiscountProduct;
import com.fct.promotion.interfaces.PageResponse;
import com.fct.promotion.interfaces.PromotionService;
import com.fct.promotion.interfaces.dto.*;
import com.fct.promotion.service.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jon on 2017/5/9.
 */
@Service("promotionService")
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private CouponPolicyManager couponPolicyManager;

    @Autowired
    private CouponCodeManager couponCodeManager;

    @Autowired
    private CouponCodeDTOManager couponCodeDTOManager;

    @Autowired
    private DiscountManager discountManager;

    @Autowired
    private OrderManager orderManager;

    @Autowired
    private DiscountProductManager discountProductManager;

    @Autowired
    private DiscountProductDTOManager discountProductDTOManager;

    @Autowired
    private CouponSpareCodeManager couponSpareCodeManager;

    public CouponPolicy saveCouponPolicy(CouponPolicy policy) {
        try {
            return couponPolicyManager.add(policy);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void auditCouponPolicy(Integer policyId, Boolean pass, Integer userId)
    {
        try {
            couponPolicyManager.audit(policyId,pass,userId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    //获取优惠券策略对象
    public CouponPolicy getCouponPolicy(Integer policyId)
    {
        try {
            return couponPolicyManager.findById(policyId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PageResponse<CouponPolicy> findCouponPolicy(Integer typeId,Integer fetchType,Integer status,String startTime,
                                               String endTime,Integer pageIndex, Integer pageSize)
    {
        try {
            return couponPolicyManager.findAll(typeId,fetchType,status,startTime,endTime,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<CouponPolicy> findCanReceiveCouponPolicy(Integer productId)
    {
        try {
            return couponPolicyManager.findByCanReceive(productId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Integer getReceiveCountByProduct(Integer productId)
    {
        try {
            return couponPolicyManager.canReceiveCountByProduct(productId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return 0;
    }

    public List<Integer> findReceivedPolicyId(Integer memberId,List<Integer> policyIds)
    {
        try {

            return couponCodeManager.findReceivedPolicyId(memberId,policyIds);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<CouponCodeDTO> findMemberCouponCode(Integer policyId,Integer memberId,String code,Integer status,
                                                    Boolean isValid,Integer pageIndex, Integer pageSize)
    {
        try {
            return couponCodeDTOManager.findMemberCouponCode(policyId,memberId,code,status,
                    isValid,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Integer getMemberCouponCodeCount(Integer policyId,Integer memberId,String code,Integer status,
                                            Boolean isValid)
    {
        try {
            return couponCodeDTOManager.getMemberCouponCodeCount(policyId,memberId,code,status,
                    isValid);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public String receiveCouponCode(Integer memberId,Integer policyId)
    {
        try {
            return couponCodeManager.receive(memberId,policyId);
        }
        catch (IllegalArgumentException exp)
        {
          throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public CouponCodeDTO getCouponCodeDTOByOrder(Integer memberId, List<OrderProductDTO> productList)
    {
        try {
            return couponCodeDTOManager.findByMemberId(memberId,productList,"");
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public CouponCodeDTO validCouponCode(Integer memberId,List<OrderProductDTO> productList,String couponCode)
    {
        try {
            return couponCodeDTOManager.findByMemberId(memberId,productList,couponCode);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public CouponCodeDTO getCouponCodeDTOByCode(String code)
    {
        try {
            return couponCodeDTOManager.findByCode(code);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void useCouponCode(String code)
    {
        try {
            couponCodeManager.setCodeUsed(code);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void cancelUseCouponCode(String code)
    {
        try {
            couponCodeManager.cancelCodeUsed(code);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void saveDiscount(Discount discount)
    {
        try {
            //无法进入
            if(discount.getId() != null && discount.getId()>0)
            {
                discountManager.update(discount);
            }
            else {
                discountManager.add(discount);
            }
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public Discount getDiscountById(Integer discountId)
    {
        try {
            return discountManager.findById(discountId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public DiscountProductDTO getDiscountByProduct(Integer productId)
    {
        try {
            return discountProductDTOManager.findByProductId(productId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void auditDiscount(Integer discountId,Boolean pass,Integer userId)
    {
        try {
            discountManager.audit(discountId,pass,userId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<Discount> findDiscount(String name,String goodsName,Integer status, String startTime, String endTime, Integer pageIndex,
                                               Integer pageSize)
    {
        try {
            return discountManager.findAll(name,goodsName,status,startTime,endTime,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<DiscountProduct> findDiscountProduct(Integer discountId)
    {
        try {
            return discountProductManager.findByDiscountId(discountId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public DisCountDTO getDisCountDTOById(Integer discountId)
    {
        try {
            DisCountDTO dto = new DisCountDTO();
            dto.setDiscount(discountManager.findById(discountId));
            dto.setProductList(discountProductManager.findByDiscountId(discountId));
            return dto;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<DiscountProductDTO> findDiscountProductDTO(List<Integer> productIds, int filterNoBegin)
    {
        try {
            return discountProductDTOManager.findByProduct(productIds,filterNoBegin);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Integer useCouponCodeDiscount(String orderId,Integer memberId,Integer memberGradeId,List<OrderProductDTO> products,
                                  String couponCode)
    {
        try {
            return orderManager.use(orderId,memberId,products,couponCode,memberGradeId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return 0;
    }

    public DiscountCouponDTO getPromotion(Integer memberId, List<OrderProductDTO> products, String couponCode)
    {
        try {
            return discountProductDTOManager.getDiscountCoupon(memberId,products,couponCode);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void couponCodeExpirseTask()
    {
        try {
            couponCodeManager.setStatusExpire();
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void generateSpareCodeTask()
    {
        try {
            couponSpareCodeManager.task();
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void generateCouponCodeForSystemTask()
    {
        try {
            couponPolicyManager.task();
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
}

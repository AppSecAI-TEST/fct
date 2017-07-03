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
        return couponPolicyManager.add(policy);
    }

    public void auditCouponPolicy(Integer policyId, Boolean pass, Integer userId)
    {
        couponPolicyManager.audit(policyId,pass,userId);
    }

    //获取优惠券策略对象
    public CouponPolicy getCouponPolicy(Integer policyId)
    {
        return couponPolicyManager.findById(policyId);
    }

    public PageResponse<CouponPolicy> findCouponPolicy(Integer typeId,Integer fetchType,Integer status,String startTime,
                                               String endTime,Integer pageIndex, Integer pageSize)
    {
        return couponPolicyManager.findAll(typeId,fetchType,status,startTime,endTime,pageIndex,pageSize);
    }

    public List<CouponPolicy> findCanReceiveCouponPolicy(Integer productId)
    {
        return couponPolicyManager.findByCanReceive(productId);
    }

    public Integer getReceiveCountByProduct(Integer productId)
    {
        return couponPolicyManager.canReceiveCountByProduct(productId);
    }

    public List<Integer> findReceivedPolicyId(Integer memberId,List<Integer> policyIds)
    {
        return couponCodeManager.findReceivedPolicyId(memberId,policyIds);
    }

    public List<CouponCodeDTO> findMemberCouponCode(Integer policyId,Integer memberId,String code,Integer status,
                                                    Boolean isValid,Integer pageIndex, Integer pageSize)
    {
        return couponCodeDTOManager.findMemberCouponCode(policyId,memberId,code,status,
                isValid,pageIndex,pageSize);
    }

    public Integer getMemberCouponCodeCount(Integer policyId,Integer memberId,String code,Integer status,
                                            Boolean isValid)
    {
        return couponCodeDTOManager.getMemberCouponCodeCount(policyId,memberId,code,status,
                isValid);
    }

    public String receiveCouponCode(Integer memberId,Integer policyId)
    {
        return couponCodeManager.receive(memberId,policyId);
    }

    public CouponCodeDTO getCouponCodeDTOByOrder(Integer memberId, List<OrderProductDTO> productList)
    {
        return couponCodeDTOManager.findByMemberId(memberId,productList,"");
    }

    public CouponCodeDTO validCouponCode(Integer memberId,List<OrderProductDTO> productList,String couponCode)
    {
        return couponCodeDTOManager.findByMemberId(memberId,productList,couponCode);
    }

    public CouponCodeDTO getCouponCodeDTOByCode(String code)
    {
        return couponCodeDTOManager.findByCode(code);
    }

    public void useCouponCode(String code)
    {
        couponCodeManager.setCodeUsed(code);
    }

    public void cancelUseCouponCode(String code)
    {
        couponCodeManager.cancelCodeUsed(code);
    }

    public void saveDiscount(Discount discount)
    {
        //无法进入
        if(discount.getId() != null && discount.getId()>0)
        {
            discountManager.update(discount);
        }
        else {
            discountManager.add(discount);
        }
    }

    public Discount getDiscountById(Integer discountId)
    {
        return discountManager.findById(discountId);
    }

    public DiscountProductDTO getDiscountByProduct(Integer productId)
    {
       return discountProductDTOManager.findByProductId(productId);
    }

    public void auditDiscount(Integer discountId,Boolean pass,Integer userId)
    {
        discountManager.audit(discountId,pass,userId);
    }

    public PageResponse<Discount> findDiscount(String name,String goodsName,Integer status, String startTime, String endTime, Integer pageIndex,
                                               Integer pageSize)
    {
        return discountManager.findAll(name,goodsName,status,startTime,endTime,pageIndex,pageSize);
    }

    public List<DiscountProduct> findDiscountProduct(Integer discountId)
    {
        return discountProductManager.findByDiscountId(discountId);
    }

    public DisCountDTO getDisCountDTOById(Integer discountId)
    {
        DisCountDTO dto = new DisCountDTO();
        dto.setDiscount(discountManager.findById(discountId));
        dto.setProductList(discountProductManager.findByDiscountId(discountId));
        return dto;
    }

    public List<DiscountProductDTO> findDiscountProductDTO(List<Integer> productIds, int filterNoBegin)
    {
        return discountProductDTOManager.findByProduct(productIds,filterNoBegin);
    }

    public Integer useCouponCodeDiscount(String orderId,Integer memberId,Integer memberGradeId,List<OrderProductDTO> products,
                                  String couponCode)
    {
        return orderManager.use(orderId,memberId,products,couponCode,memberGradeId);
    }

    public DiscountCouponDTO getPromotion(Integer memberId, List<OrderProductDTO> products, String couponCode)
    {
        return discountProductDTOManager.getDiscountCoupon(memberId,products,couponCode);
    }

    public void couponCodeExpirseTask()
    {
        couponCodeManager.setStatusExpire();
    }

    public void generateSpareCodeTask()
    {
        couponSpareCodeManager.task();
    }

    public void generateCouponCodeForSystemTask()
    {
        couponPolicyManager.task();
    }
}

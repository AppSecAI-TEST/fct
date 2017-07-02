package com.fct.promotion.interfaces;

import com.fct.promotion.data.entity.CouponPolicy;
import com.fct.promotion.data.entity.Discount;
import com.fct.promotion.data.entity.DiscountProduct;
import com.fct.promotion.interfaces.dto.*;

import java.util.List;

/**
 * Created by jon on 2017/5/9.
 * jon miss nancy very much
 */
public interface PromotionService {

    CouponPolicy saveCouponPolicy(CouponPolicy policy);

    void auditCouponPolicy(Integer policyId, Boolean pass,Integer userId);

    //获取优惠券策略对象
    CouponPolicy getCouponPolicy(Integer policyId);

    PageResponse<CouponPolicy> findCouponPolicy(Integer typeId,Integer fetchType,Integer status, String startTime,
                                        String endTime,Integer pageIndex, Integer pageSize);

    List<CouponPolicy> findCanReceiveCouponPolicy();

    List<Integer> findReceivedPolicyId(Integer memberId,List<Integer> policyIds);

    List<CouponCodeDTO> findMemberCouponCode(Integer policyId,Integer memberId,String code,Integer status,
                                             Boolean isValid,Integer pageIndex, Integer pageSize);

    Integer getMemberCouponCodeCount(Integer policyId,Integer memberId,String code,Integer status,
                                     Boolean isValid);

    String receiveCouponCode(Integer memberId,Integer policyId);

    CouponCodeDTO getCouponCodeDTOByOrder(Integer memberId, List<OrderProductDTO> productList);

    CouponCodeDTO validCouponCode(Integer memberId,List<OrderProductDTO> productList,String couponCode);

    CouponCodeDTO getCouponCodeDTOByCode(String code);

    void useCouponCode(String code);

    void cancelUseCouponCode(String code);

    void saveDiscount(Discount discount);

    Discount getDiscountById(Integer discountId);

    void auditDiscount(Integer discount,Boolean pass,Integer userId);

    PageResponse<Discount> findDiscount(String name,String goodsName,Integer status,String startTime,String endTime,Integer pageIndex,Integer pageSize);

    List<DiscountProduct> findDiscountProduct(Integer discountId);

    List<DiscountProductDTO> findDiscountProductDTO(List<Integer> productIds, int filterNoBegin);

    DisCountDTO getDisCountDTOById(Integer discountId);

    Integer useCouponCodeDiscount(String orderId,Integer memberId,Integer memberLevel,List<OrderProductDTO> products,
                                   String couponCode);

    DiscountCouponDTO getPromotion(Integer memberId,List<OrderProductDTO> products,String couponCode);

    void couponCodeExpirseTask();

    void generateSpareCodeTask();

    void generateCouponCodeForSystemTask();

}

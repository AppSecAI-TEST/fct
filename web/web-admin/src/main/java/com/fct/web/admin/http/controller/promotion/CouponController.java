package com.fct.web.admin.http.controller.promotion;

import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.PageUtil;
import com.fct.mall.data.entity.Goods;
import com.fct.promotion.data.entity.CouponPolicy;
import com.fct.promotion.interfaces.PageResponse;
import com.fct.promotion.interfaces.PromotionService;
import com.fct.web.admin.http.cache.CacheGoodsManager;
import com.fct.core.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jon on 2017/6/14.
 */

@Controller
@RequestMapping(value = "/promotion/coupon")
public class CouponController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private CacheGoodsManager cacheGoodsManager;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Integer typeid,Integer fetchtype,Integer status,String starttime,String endtime,Integer page,Model model)
    {
        status = ConvertUtils.toInteger(status,-1);
        typeid = ConvertUtils.toInteger(typeid,-1);
        fetchtype =ConvertUtils.toInteger(fetchtype,-1);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);
        page =ConvertUtils.toPageIndex(page);

        Integer pagesize = 30;
        String pageUrl = "?page=%d";

        if(typeid>-1)
        {
            pageUrl+="&typeid="+typeid;
        }
        if(fetchtype>-1)
        {
            pageUrl+="&fetchtype="+fetchtype;
        }
        if(status>-1)
        {
            pageUrl+="&status="+status;
        }
        if(!StringUtils.isEmpty(starttime))
        {
            pageUrl += "&starttime="+starttime;
        }
        if(!StringUtils.isEmpty(endtime))
        {
            pageUrl += "&endtime="+endtime;
        }


        PageResponse<CouponPolicy> pageResponse = null;
        try {
            pageResponse = promotionService.findCouponPolicy(typeid,fetchtype,status,starttime, endtime, page, pagesize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<CouponPolicy>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("status", status);
        query.put("fetchtype", fetchtype);
        query.put("typeid", typeid);
        query.put("starttime",starttime);
        query.put("endtime",endtime);

        model.addAttribute("query", query);
        model.addAttribute("lsCoupon", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pagesize,pageUrl));

        return "/promotion/coupon/index";
    }

    @RequestMapping(value = "/upstatus", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String upStatus(Integer id,String action)
    {
        id = ConvertUtils.toInteger(id);
        action = ConvertUtils.toString(action);
        Integer status =0;
        switch (action)
        {
            case "pass":
                status =1;
                break;
            default:
                status=0;
        }

        try {
            promotionService.auditCouponPolicy(id,status==1,1);
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            //这里没有写进文件
            Constants.logger.error(Exceptions.getStackTraceAsString(exp));
            return AjaxUtil.alert("系统或网络错误，请稍候再试。");
        }

        return AjaxUtil.reload("处理成功。");
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Integer id,Model model) {

        id = ConvertUtils.toInteger(id);
        String goodsName = "";
        CouponPolicy coupon = null;
        try
        {
            if(id >0 ) {
                coupon = promotionService.getCouponPolicy(id);

                for (Goods g:cacheGoodsManager.findGoodsByIds(coupon.getProductIds())
                     ) {
                    if(!StringUtils.isEmpty(goodsName))
                    {
                        goodsName +="、";
                    }
                    goodsName += g.getName();
                }
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        if(coupon == null)
        {
            coupon = new CouponPolicy();
            coupon.setId(0);
            coupon.setUsingType(0);
            coupon.setFetchType(0);
            coupon.setTypeId(0);
        }

        List<Integer> lsSignleCount = new ArrayList<>();
        lsSignleCount.add(1);
        lsSignleCount.add(3);
        lsSignleCount.add(5);

        model.addAttribute("coupon",coupon);
        model.addAttribute("lsSignleCount",lsSignleCount);
        model.addAttribute("goodsName",goodsName);

        return "/promotion/coupon/create";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(Integer id,String name,BigDecimal amount,String productids,Integer usingtype,BigDecimal fullamount,
                       Integer fetchtype,Integer totalcount,Integer singlecount,Integer status, Integer typeid,
                       String starttime,String endtime) {

        id = ConvertUtils.toInteger(id);
        name = ConvertUtils.toString(name);
        amount = ConvertUtils.toBigDeciaml(amount);
        productids =ConvertUtils.toString(productids);
        usingtype = ConvertUtils.toInteger(usingtype);
        fullamount = ConvertUtils.toBigDeciaml(fullamount);
        fetchtype = ConvertUtils.toInteger(fetchtype);
        totalcount =ConvertUtils.toInteger(totalcount);
        singlecount =ConvertUtils.toInteger(singlecount);
        status =ConvertUtils.toInteger(status);
        typeid =ConvertUtils.toInteger(typeid);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);

        try {

            CouponPolicy coupon = null;
            if (id > 0) {
                coupon = promotionService.getCouponPolicy(id);
            } else {
                coupon = new CouponPolicy();
                coupon.setId(0);

                coupon.setFetchType(fetchtype);
                coupon.setTypeId(typeid);
                coupon.setCreateUserId(1);
                coupon.setReceivedCount(0);
                coupon.setAmount(amount);
            }

            coupon.setName(name);
            coupon.setProductIds(productids);
            coupon.setUsingType(usingtype);
            coupon.setFullAmount(fullamount);
            coupon.setTotalCount(totalcount);
            coupon.setSingleCount(singlecount);
            coupon.setAuditStatus(status);
            coupon.setStartTime(DateUtils.parseStringtoHour(starttime));
            coupon.setEndTime(DateUtils.parseStringtoHour(endtime));

            //这里调用
            promotionService.saveCouponPolicy(coupon);
        }
        catch (IllegalArgumentException exp)
        {
            return AjaxUtil.alert(exp.getMessage());
        }
        catch (Exception exp)
        {
            Constants.logger.error(Exceptions.getStackTraceAsString(exp));
            return AjaxUtil.alert("系统或网络错误，请稍候再试。");
        }

        return AjaxUtil.goUrl("/promotion/coupon","保存数据成功");
    }
}

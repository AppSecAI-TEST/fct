package com.fct.web.admin.http.controller.promotion;

import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.promotion.data.entity.CouponPolicy;
import com.fct.promotion.interfaces.PageResponse;
import com.fct.promotion.interfaces.PromotionService;
import com.fct.promotion.interfaces.dto.CouponCodeDTO;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jon on 2017/8/3.
 */
@Controller
@RequestMapping(value = "/promotion/coupon")

public class CouponCodeController extends BaseController {

    @Autowired
    private PromotionService promotionService;

    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public String index(Integer policyid,String selkey,String selvalue,Integer status,Integer page,Model model)
    {
        status = ConvertUtils.toInteger(status,-1);
        selkey = ConvertUtils.toString(selkey);
        selvalue = ConvertUtils.toString(selvalue);
        page =ConvertUtils.toPageIndex(page);
        policyid = ConvertUtils.toInteger(policyid);

        Integer pagesize = 30;
        StringBuilder sb = new StringBuilder();
        sb.append("?page=%d");

        Integer memberId = 0;
        String code = "";

        if(status>-1)
        {
            sb.append("&status="+status);
        }
        if(policyid>0)
        {
            sb.append("&policyid="+policyid);
        }
        if(!StringUtils.isEmpty(selvalue))
        {
            if(selkey.equals("code")) {
                code = selvalue;
            }else
            {
                memberId = Integer.valueOf(selvalue);
            }
            sb.append("&selkey="+selkey+"&selvalue="+selvalue);
        }
        List<CouponCodeDTO> lsCoupon = new ArrayList<>();
        Integer itemCount = 0;
        try {
            lsCoupon = promotionService.findMemberCouponCode(policyid,memberId,code,status,
                    false, page, pagesize);
            itemCount = promotionService.getMemberCouponCodeCount(policyid,memberId,code,status,
                    false);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }

        Map<String,Object> query = new HashMap<>();
        query.put("status", status);
        query.put("selkey", selkey);
        query.put("selvalue", selvalue);
        query.put("policyid", policyid);

        model.addAttribute("query", query);
        model.addAttribute("lsCoupon", lsCoupon);
        model.addAttribute("pageHtml", PageUtil.getPager(itemCount,page,
                pagesize,sb.toString()));

        return "/promotion/coupon/code";
    }
}

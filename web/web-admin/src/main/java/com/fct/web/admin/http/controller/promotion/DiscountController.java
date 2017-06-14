package com.fct.web.admin.http.controller.promotion;

import com.fct.common.utils.ConvertUtils;
import com.fct.common.utils.PageUtil;
import com.fct.promotion.data.entity.Discount;
import com.fct.promotion.interfaces.PageResponse;
import com.fct.promotion.interfaces.PromotionService;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jon on 2017/6/14.
 */

@Controller
@RequestMapping(value = "/promotion/discount")
public class DiscountController {

    @Autowired
    private PromotionService promotionService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String selkey,String selvalue, Integer status,String starttime,String endtime,Integer page,Model model)
    {
        selkey = ConvertUtils.toString(selkey);
        selvalue = ConvertUtils.toString(selvalue);
        status = ConvertUtils.toInteger(status,-1);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);
        page =ConvertUtils.toPageIndex(page);

        Integer pagesize = 30;
        String pageUrl = "?page=%d";

        String goodsname="";
        String name="";
        if(selkey == "name")
        {
            name = selvalue;
        }
        else
        {
            goodsname = selvalue;
        }

        if(!StringUtils.isEmpty(selvalue))
        {
            pageUrl += "&selkey="+selkey +"&selvalue="+selvalue;
        }
        if(status>-1)
        {
            pageUrl+="&status="+status;
        }
        if(!StringUtils.isEmpty(selvalue))
        {
            pageUrl += "&selkey="+selkey +"&selvalue="+selvalue;
        }
        if(!StringUtils.isEmpty(starttime))
        {
            pageUrl += "&starttime="+starttime;
        }
        if(!StringUtils.isEmpty(endtime))
        {
            pageUrl += "&endtime="+endtime;
        }


        PageResponse<Discount> pageResponse = null;
        try {
            pageResponse = promotionService.findDiscount(name,goodsname,status,starttime, endtime, page, pagesize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<Discount>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("selkey", selkey);
        query.put("selvalue", selvalue);
        query.put("status", status);
        query.put("goodsname", goodsname);
        query.put("starttime",starttime);
        query.put("endtime",endtime);

        model.addAttribute("query", query);
        model.addAttribute("lsDiscount", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pagesize,pageUrl));

        return "/promotion/discount/index";
    }
}

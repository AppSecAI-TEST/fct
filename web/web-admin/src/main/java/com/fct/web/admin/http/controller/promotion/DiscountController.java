package com.fct.web.admin.http.controller.promotion;

import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.DateUtils;
import com.fct.core.utils.PageUtil;
import com.fct.promotion.data.entity.Discount;
import com.fct.promotion.data.entity.DiscountProduct;
import com.fct.promotion.interfaces.PageResponse;
import com.fct.promotion.interfaces.PromotionService;
import com.fct.promotion.interfaces.dto.DisCountDTO;
import com.fct.web.admin.http.cache.CacheGoodsManager;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.core.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by jon on 2017/6/14.
 */

@Controller
@RequestMapping(value = "/promotion/discount")
public class DiscountController extends BaseController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private CacheGoodsManager cacheGoodsManager;

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
        StringBuilder sb = new StringBuilder();
        sb.append("?page=%d");

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
            sb.append("&selkey="+selkey +"&selvalue="+selvalue);
        }
        if(status>-1)
        {
            sb.append("&status="+status);
        }
        if(!StringUtils.isEmpty(selvalue))
        {
            sb.append("&selkey="+selkey +"&selvalue="+selvalue);
        }
        if(!StringUtils.isEmpty(starttime))
        {
            sb.append("&starttime="+starttime);
        }
        if(!StringUtils.isEmpty(endtime))
        {
            sb.append("&endtime="+endtime);
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
                pagesize,sb.toString()));

        return "/promotion/discount/index";
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
            promotionService.auditDiscount(id,status == 1,1);
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
        Discount discount = null;
        try
        {
            if(id >0 ) {
                DisCountDTO disCountDTO = promotionService.getDisCountDTOById(id);
                discount = disCountDTO.getDiscount();
                discount.setProductList(disCountDTO.getProductList());
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        if(discount == null)
        {
            discount = new Discount();
            discount.setId(0);
        }


        List<Integer> lsSignleCount = new ArrayList<>();
        lsSignleCount.add(0);
        lsSignleCount.add(1);
        lsSignleCount.add(3);
        lsSignleCount.add(5);

        model.addAttribute("discount",discount);
        model.addAttribute("lsSignleCount",lsSignleCount);
        model.addAttribute("cacheGoods",cacheGoodsManager);
        return "/promotion/discount/create";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(Integer id,String name,String banner,String content,String starttime,String endtime,
                       Integer closetime,Integer seckill,@RequestParam(required = false) String g_id,
                       @RequestParam(required = false) String p_id,@RequestParam(required = false) String g_name,
                       @RequestParam(required = false) String g_discount,@RequestParam(required = false) String g_singlecount) {

        id = ConvertUtils.toInteger(id);
        name = ConvertUtils.toString(name);
        banner = ConvertUtils.toString(banner);
        content = ConvertUtils.toString(content);
        closetime = ConvertUtils.toInteger(closetime);
        seckill = ConvertUtils.toInteger(seckill);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);

        String[] arr_g_id = ConvertUtils.toStringArray(g_id);
        String[] arr_p_id = ConvertUtils.toStringArray(p_id);
        String[] arr_g_name = ConvertUtils.toStringArray(g_name);
        String[] arr_g_discount = ConvertUtils.toStringArray(g_discount);
        String[] arr_g_singlecount = ConvertUtils.toStringArray(g_singlecount);

        try {

            Discount discount = null;
            if (id > 0) {
                discount = promotionService.getDiscountById(id);
            } else {
                discount = new Discount();
                discount.setId(0);
            }

            List<DiscountProduct> lsGoods = new ArrayList<>();
            if(arr_g_id!=null && arr_g_id.length>0)
            {
                if(arr_g_id.length != arr_g_name.length ||
                        arr_g_id.length != arr_g_name.length ||
                        arr_g_id.length != arr_g_discount.length ||
                        arr_g_id.length != arr_g_singlecount.length)
                {
                    return AjaxUtil.alert("宝贝数据不完整。");
                }

                for(int i=0;i<arr_g_id.length;i++)
                {
                    DiscountProduct p = new DiscountProduct();
                    p.setId(Integer.valueOf(arr_p_id[i]));
                    p.setCreateUserId(0);
                    p.setDiscountRate(new BigDecimal(arr_g_discount[i]).divide(new BigDecimal(10)));
                    p.setProductId(Integer.valueOf(arr_g_id[i]));
                    p.setProductName(arr_g_name[i]);
                    p.setDiscountId(discount.getId());
                    p.setSingleCount(Integer.valueOf(arr_g_singlecount[i]));

                    lsGoods.add(p);
                }
            }
            discount.setName(name);
            discount.setBanner(banner);
            discount.setContent(content);
            discount.setMemberGradeId(0);
            discount.setOrderCloseTime(closetime);
            discount.setNotStartCanNotBuy(seckill);
            discount.setStartTime(DateUtils.parseStringtoHour(starttime));
            discount.setEndTime(DateUtils.parseStringtoHour(endtime));
            discount.setProductList(lsGoods);
            discount.setCreateUserId(1);

            //这里调用
            promotionService.saveDiscount(discount);
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

        return AjaxUtil.reload("保存数据成功");
    }
}

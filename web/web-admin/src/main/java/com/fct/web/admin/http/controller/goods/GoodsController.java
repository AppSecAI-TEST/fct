package com.fct.web.admin.http.controller.goods;

import com.fct.mall.data.entity.Goods;
import com.fct.mall.data.entity.GoodsCategory;
import com.fct.mall.data.entity.GoodsGrade;
import com.fct.mall.interfaces.MallService;
import com.alibaba.dubbo.common.URL;
import com.fct.common.exceptions.Exceptions;
import com.fct.common.utils.ConvertUtils;
import com.fct.common.utils.PageUtil;
import com.fct.mall.data.entity.*;
import com.fct.mall.interfaces.PageResponse;
import com.fct.web.admin.http.cache.CacheGoodsManager;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;


/**
 * Created by jon on 2017/6/1.
 */

@Controller
@RequestMapping(value = "/goods")
public class GoodsController extends BaseController {

    @Autowired
    private MallService mallService;

    @Autowired
    private CacheGoodsManager cacheGoodsManager;

    /**
     * 获取商品品级
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String catecode,String name,Integer status,Integer gradeid,Integer artistid,
                        Integer materialid,Integer page, Model model) {

        catecode = ConvertUtils.toString(catecode);
        name = ConvertUtils.toString(name);
        status =ConvertUtils.toInteger(status,-1);
        gradeid =ConvertUtils.toInteger(gradeid);
        artistid =ConvertUtils.toInteger(artistid);
        materialid =ConvertUtils.toInteger(materialid);
        page =ConvertUtils.toPageIndex(page);

        List<GoodsCategory> lsCategory = cacheGoodsManager.findGoodsCategoryByParent();//这样引用出错
        List<GoodsGrade> lsGrade = cacheGoodsManager.findGoodsGrade();
        Integer pageSize = 30;
        String pageUrl = "?page=%d";
        if(!StringUtils.isEmpty(name))
        {
            pageUrl +="&q="+ URL.encode(name);
        }
        if(!StringUtils.isEmpty(catecode))
        {
            pageUrl +="&catecode="+ catecode;
        }
        if(gradeid>0)
        {
            pageUrl +="&gradeid="+ gradeid;
        }
        if(artistid>0)
        {
            pageUrl +="&artistid="+ artistid;
        }
        if(materialid>0)
        {
            pageUrl +="&materialid="+ materialid;
        }
        PageResponse<Goods> pageResponse = null;

        try {

            pageResponse = mallService.findGoods(name, catecode, gradeid, materialid, artistid,
                    0, 0, status, page, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<Goods>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("name", name);
        query.put("status", status);
        query.put("gradeid", gradeid);
        query.put("artistid", artistid);
        query.put("parentCate", lsCategory);
        query.put("gradeList", lsGrade);
        query.put("materialid",materialid);
        query.put("catecode",catecode);

        model.addAttribute("query", query);
        model.addAttribute("lsGoods", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,pageUrl));
        model.addAttribute("cache", cacheGoodsManager);

        return "goods/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Integer id, Model model) {
        id = ConvertUtils.toInteger(id);
        String materialName ="";
        Goods goods =null;
        if(id>0) {
            goods = mallService.getGoods(id);
            if(!StringUtils.isEmpty(goods.getMaterialId())) {
                String materialid = goods.getMaterialId();
                materialName = cacheGoodsManager.getMaterialName(goods.getMaterialId());
                materialid = materialid.substring(1, materialid.length() - 1);
                goods.setMaterialId(materialid); //为了去掉前后各一个, 避免添加的时候重复。
            }

        }
        if (goods == null) {
            goods = new Goods();
            goods.setId(0);
            goods.setArtistIds("");
            goods.setCategoryCode("");
            goods.setMaterialId("");
        }
        List<GoodsCategory> categoryList = cacheGoodsManager.findGoodsCategoryByParent();
        List<GoodsGrade> gradeList = cacheGoodsManager.findGoodsGrade();

        model.addAttribute("materialName",materialName);
        model.addAttribute("gradeList", gradeList);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("goods", goods);
        return "goods/create";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(HttpServletRequest request,String artistId,String materialId, @RequestParam(required = false) String ext_id,
                       @RequestParam(required = false) String ext_name,@RequestParam(required = false) String ext_code,@RequestParam(required = false) String ext_marketPrice,
                       @RequestParam(required = false) String ext_salePrice,@RequestParam(required = false) String ext_commission,@RequestParam(required = false) String ext_stockCount)
    {
        String categorycode = ConvertUtils.toString(request.getParameter("categorycode"));
        String content = ConvertUtils.toString(request.getParameter("content"));
        String name = ConvertUtils.toString(request.getParameter("name"));
        String subTitle = ConvertUtils.toString(request.getParameter("subTitle"));
        String defaultImage = ConvertUtils.toString(request.getParameter("defaultImage"));
        String videoId = ConvertUtils.toString(request.getParameter("videoId"));
        String videoImg = ConvertUtils.toString(request.getParameter("videoImg"));
        String code = ConvertUtils.toString(request.getParameter("code"));
        BigDecimal marketPrice = ConvertUtils.toBigDeciaml(request.getParameter("marketPrice"));
        BigDecimal salePrice = ConvertUtils.toBigDeciaml(request.getParameter("salePrice"));
        BigDecimal commission = ConvertUtils.toBigDeciaml(request.getParameter("commission"));
        Integer stockCount = ConvertUtils.toInteger(request.getParameter("stockCount"));
        Integer advanceSaleDays = ConvertUtils.toInteger(request.getParameter("advanceSaleDays"));
        Integer sortIndex = ConvertUtils.toInteger(request.getParameter("sortIndex"));
        Integer id = ConvertUtils.toInteger(request.getParameter("id"));
        Integer gradeId = ConvertUtils.toInteger(request.getParameter("gradeId"));
        Integer status = ConvertUtils.toInteger(request.getParameter("status"));
        Integer minVolume = ConvertUtils.toInteger(request.getParameter("minVolume"));
        Integer maxVolume = ConvertUtils.toInteger(request.getParameter("maxVolume"));
        String intro = ConvertUtils.toString(request.getParameter("intro"));

        artistId = ConvertUtils.toString(artistId);
        materialId = ConvertUtils.toString(materialId);

        String[] arr_ext_id = ConvertUtils.toStringArray(ext_id);
        String[] arr_ext_name = ConvertUtils.toStringArray(ext_name);
        String[] arr_ext_code = ConvertUtils.toStringArray(ext_code);
        String[] arr_ext_marketPrice = ConvertUtils.toStringArray(ext_marketPrice);
        String[] arr_ext_salePrice = ConvertUtils.toStringArray(ext_salePrice);
        String[] arr_ext_commission = ConvertUtils.toStringArray(ext_commission);
        String[] arr_ext_stockCount = ConvertUtils.toStringArray(ext_stockCount);

        List<GoodsSpecification> lsSpec = new ArrayList<>();
        if(arr_ext_name!=null && arr_ext_name.length>0)
        {
            if(arr_ext_id.length != arr_ext_marketPrice.length ||
                    arr_ext_id.length != arr_ext_salePrice.length ||
                    arr_ext_id.length != arr_ext_commission.length ||
                    arr_ext_id.length != arr_ext_stockCount.length ||
                    arr_ext_id.length != arr_ext_code.length)
            {
                return AjaxUtil.alert("规格数据不完整。");
            }

           for(int i=0;i<arr_ext_id.length;i++)
           {
               GoodsSpecification spec = new GoodsSpecification();
               spec.setId(Integer.valueOf(arr_ext_id[i]));
               spec.setCode(arr_ext_code[i]);
               spec.setName(arr_ext_name[i]);
               spec.setCommission(new BigDecimal(arr_ext_commission[i]));
               spec.setSalePrice(new BigDecimal(arr_ext_salePrice[i]));
               spec.setMarketPrice(new BigDecimal(arr_ext_marketPrice[i]));
               spec.setStockCount(Integer.valueOf(arr_ext_stockCount[i]));

               lsSpec.add(spec);
           }
        }

        Goods goods = null;
        if(id>0)
        {
            goods = mallService.getGoods(id);
        }
        else
        {
            goods = new Goods();
            goods.setId(0);
        }
        String[] arrImg = defaultImage.split(",");
        goods.setSpecification(lsSpec);
        goods.setName(name);
        goods.setSubTitle(subTitle);
        goods.setCategoryCode(categorycode);
        goods.setContent(content);
        goods.setDefaultImage(arrImg[0]); //取第一张为默认
        goods.setMultiImages(defaultImage);
        goods.setVideoId(videoId);
        goods.setVideoImg(videoImg);
        goods.setCode(code);
        goods.setMarketPrice(marketPrice);
        goods.setSalePrice(salePrice);
        goods.setCommission(commission);
        goods.setStockCount(stockCount);
        goods.setMaterialId(","+materialId+",");
        goods.setMinVolume(minVolume);
        goods.setMaxVolume(maxVolume);
        goods.setAdvanceSaleDays(advanceSaleDays);
        goods.setSortIndex(sortIndex);
        goods.setIntro(intro);
        goods.setGradeId(gradeId);
        goods.setStatus(status);

        goods.setArtistIds(","+artistId+",");

        try
        {
            mallService.saveGoods(goods);
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
        return AjaxUtil.goUrl("/goods","保存宝贝信息成功");
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String delete(Integer id)
    {
        id = ConvertUtils.toInteger(id);
        if(id<=0)
        {
            return AjaxUtil.alert("id不正确。");
        }
        try
        {
            mallService.deleteGoods(id);
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
        return AjaxUtil.reload("删除宝贝成功。");
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    public String select(String ids,@RequestParam(required = false)String disabled,
                         @RequestParam(required = false)String names,Model model) {

        ids = ConvertUtils.toString(ids);
        disabled = ConvertUtils.toString(disabled);
        names = ConvertUtils.toString(names);
        List<GoodsCategory> lsCategory = cacheGoodsManager.findGoodsCategoryByParent();//这样引用出错
        try
        {
            names = URLDecoder.decode(names, "UTF-8");
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }

        model.addAttribute("category",lsCategory);
        model.addAttribute("ids", ids);
        model.addAttribute("names", names);
        model.addAttribute("disabled", disabled);
        return "goods/select";
    }
    @ResponseBody
    @RequestMapping(value = "/ajaxload", method = RequestMethod.GET,produces="text/html;charset=UTF-8")
    public String ajaxLoad(String q,String cateid,String ids,@RequestParam(required = false)String disabled)
    {
        q = ConvertUtils.toString(q);
        cateid = ConvertUtils.toString(cateid);
        ids = ConvertUtils.toString(ids);
        disabled = ConvertUtils.toString(disabled);

        PageResponse<Goods> pageResponse = mallService.findGoods(q, cateid, 0, 0, 0,
                0, 0, 1, 1, 25);

        StringBuilder sb = new StringBuilder();

        String[] arrId = ids.split(",");
        List<String> idList = Arrays.asList(arrId);

        for (Goods g:pageResponse.getElements()
                ) {
            String checked = "";
            if(idList.contains(g.getId().toString()))
            {
                checked = " checked=\"checked\"";
                if(StringUtils.isEmpty(disabled)){
                    checked += "disabled=\"disabled\"";
                }
            }

            String json = "{id:'"+ g.getId()+"',name:'"+ g.getName() +"',price:'"+ g.getSalePrice() +"'}";

            sb.append("<tr>");
            sb.append("<td>"+ g.getName() +"</td>");
            sb.append("<td>"+ g.getCode() +"</td>");
            sb.append("<td>¥"+ g.getSalePrice() +"</td>");
            sb.append("<td>");
            sb.append("<input type=\"checkbox\" class=\"goodsCheck\" value=\""+g.getId()+"\" data-json=\""+ json +"\" name=\"goodscheck\" "+checked+"/>");
            sb.append("</td>");
            sb.append("</tr>");
        }
        return sb.toString();
    }
}

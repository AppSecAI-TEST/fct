package com.fct.web.admin.http.controller.goods;

import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.mall.data.entity.GoodsMaterial;
import com.fct.mall.data.entity.OrderComment;
import com.fct.mall.interfaces.MallService;
import com.fct.mall.interfaces.PageResponse;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.core.utils.AjaxUtil;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jon on 2017/6/14.
 */
@Controller
@RequestMapping(value = "/goods/comment")
public class CommentController extends BaseController{

    @Autowired
    private MallService mallService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Integer goodsid,String selkey,String selvalue,Integer status,String starttime,String endtime,
                        Integer page,Model model) {

        selkey = ConvertUtils.toString(selkey);
        selvalue = ConvertUtils.toString(selvalue);
        status = ConvertUtils.toInteger(status,-1);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);
        page =ConvertUtils.toPageIndex(page);
        goodsid =ConvertUtils.toInteger(goodsid);

        Integer pagesize = 30;
        String pageUrl = "?page=%d";

        String cellphone="";
        String orderid="";
        if(selkey == "orderid")
        {
            orderid = selvalue;
        }
        else
        {
            cellphone = selvalue;
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

        PageResponse<OrderComment> pageResponse = null;
        try {
            pageResponse = mallService.findOrderComment(goodsid, 0, cellphone, orderid,
                    status, starttime, endtime, page, pagesize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<OrderComment>();
        }

        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pagesize,pageUrl));


        Map<String,Object> query = new HashMap<>();
        query.put("selkey", selkey);
        query.put("selvalue", selvalue);
        query.put("status", status);
        query.put("starttime",starttime);
        query.put("endtime",endtime);

        model.addAttribute("query", query);
        model.addAttribute("lsComment", pageResponse.getElements());

        return "goods/comment/index";
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
            case "delete":
                status=2;   //删除
                break;
            case "pass":
                status =1;
                break;
            default:
                status=0;
        }

        try {
            mallService.updateOrderCommentStatus(id,status);
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
}

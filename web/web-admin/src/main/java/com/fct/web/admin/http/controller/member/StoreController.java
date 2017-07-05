package com.fct.web.admin.http.controller.member;

import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;

import com.fct.member.data.entity.MemberStore;
import com.fct.member.interfaces.MemberService;
import com.fct.member.interfaces.PageResponse;
import com.fct.core.utils.AjaxUtil;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/member/store")
public class StoreController extends BaseController{

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String q, Integer status,String starttime,String endtime,Integer page, Model model) {

        q =ConvertUtils.toString(q);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);
        status = ConvertUtils.toInteger(status,-1);

        page = ConvertUtils.toPageIndex(page);

        Integer pageSize = 30;
        StringBuilder sb = new StringBuilder();
        sb.append("?page=%d");

        if(!StringUtils.isEmpty(q))
        {
            sb.append("&q="+ URLEncoder.encode(q));
        }
        if(status>-1)
        {
            sb.append("&status="+status);
        }
        if(!StringUtils.isEmpty(starttime))
        {
            sb.append("&starttime="+ starttime);
        }
        if(!StringUtils.isEmpty(endtime))
        {
            sb.append("&endtime="+ endtime);
        }

        PageResponse<MemberStore> pageResponse = null;

        try {

            pageResponse = memberService.findMemberStore(q,status,page,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<MemberStore>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("q", q);
        query.put("starttime", starttime);
        query.put("endtime", endtime);
        query.put("status", status);

        model.addAttribute("query", query);
        model.addAttribute("lsStore", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,sb.toString()));

        return "member/store";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String update(Integer id, String action) {
        id = ConvertUtils.toInteger(id);
        action = ConvertUtils.toString(action);

        if (id <= 0) {
            return AjaxUtil.alert("id不正确。");
        }
        if (StringUtils.isEmpty(action))
        {
            return AjaxUtil.alert("行为不正确!");
        }
        try {
            switch (action)
            {
                case "audi":
                    memberService.updateStoreStatus(id);
                    break;
            }
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
        return AjaxUtil.reload("操作数据成功。");
    }
}

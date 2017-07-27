package com.fct.web.admin.http.controller.member;

import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.member.data.entity.MemberBankInfo;
import com.fct.member.interfaces.MemberService;
import com.fct.member.interfaces.PageResponse;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/member/bank")
public class BankController extends BaseController {

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Integer selkey,String selvalue, Integer status,Integer page, Model model) {

        selkey = ConvertUtils.toInteger(selkey);
        selvalue =ConvertUtils.toString(selvalue);
        status = ConvertUtils.toInteger(status,-1);

        page = ConvertUtils.toPageIndex(page);

        Integer pageSize = 30;
        String pageUrl = "?page=%d";
        String bankname = "";
        String cellphone="";

        if(!StringUtils.isEmpty(selkey))
        {
            pageUrl +="&selkey="+ selkey +"&selvalue="+selvalue;
        }
        switch (selkey)
        {
            case 1:
                bankname = selvalue;
                break;
            case 2:
                cellphone = selvalue;
                break;
        }
        PageResponse<MemberBankInfo> pageResponse = null;

        try {

            pageResponse = memberService.findMemberBankInfo(0,cellphone,bankname,status,page,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<MemberBankInfo>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("selkey", selkey);
        query.put("selvalue", selvalue);
        query.put("status", status);

        model.addAttribute("query", query);
        model.addAttribute("lsBank", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,pageUrl));

        return "member/bank";
    }
}

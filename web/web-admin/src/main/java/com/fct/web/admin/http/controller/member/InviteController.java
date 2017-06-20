package com.fct.web.admin.http.controller.member;

import com.fct.common.utils.ConvertUtils;
import com.fct.common.utils.PageUtil;
import com.fct.member.data.entity.InviteCode;
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
@RequestMapping(value = "/member/invite")
public class InviteController extends BaseController {

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Integer selkey,String selvalue, Integer page, Model model) {

        selkey = ConvertUtils.toInteger(selkey);
        selvalue =ConvertUtils.toString(selvalue);

        page = ConvertUtils.toPageIndex(page);

        Integer pageSize = 30;
        String pageUrl = "?page=%d";
        String code = "";
        String ownerCellPhone="";
        String toCellphone = "";

        if(!StringUtils.isEmpty(selkey))
        {
            pageUrl +="&selkey="+ selkey +"&selvalue="+selvalue;
        }
        switch (selkey)
        {
            case 1:
                code = selvalue;
                break;
            case 2:
                ownerCellPhone = selvalue;
                break;
            case 3:
                toCellphone = selvalue;
                break;
        }
        PageResponse<InviteCode> pageResponse = null;

        try {

            pageResponse = memberService.findInviteCode(code,0,ownerCellPhone,toCellphone,page,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<InviteCode>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("selkey", selkey);
        query.put("selvalue", selvalue);

        model.addAttribute("query", query);
        model.addAttribute("lsCode", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,pageUrl));

        return "member/invite";
    }
}

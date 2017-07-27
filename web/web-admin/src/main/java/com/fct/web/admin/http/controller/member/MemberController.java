package com.fct.web.admin.http.controller.member;

import com.alibaba.dubbo.common.URL;
import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.MemberBankInfo;
import com.fct.member.data.entity.MemberInfo;
import com.fct.member.interfaces.MemberService;
import com.fct.member.interfaces.PageResponse;
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

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/member")
public class MemberController extends BaseController {

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String q,Integer status, String starttime, String endtime,Integer page, Model model) {

        q = ConvertUtils.toString(q);
        status =ConvertUtils.toInteger(status,-1);
        starttime = ConvertUtils.toString(starttime);
        endtime = ConvertUtils.toString(endtime);
        page = ConvertUtils.toPageIndex(page);

        Integer pageSize = 30;
        StringBuilder sb = new StringBuilder();
        sb.append("?page=%d");
        if(!StringUtils.isEmpty(q))
        {
            sb.append("&q="+ URL.encode(q));
        }
        if(status>-1)
        {
            sb.append("&authstatus="+ status);
        }
        if(!StringUtils.isEmpty(starttime))
        {
            sb.append("&starttime="+ starttime);
        }
        if(!StringUtils.isEmpty(endtime))
        {
            sb.append("&endtime="+ endtime);
        }
        PageResponse<Member> pageResponse = null;

        try {

            pageResponse = memberService.findMember(q,status,starttime,endtime,page,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<Member>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("q", q);
        query.put("status", status);
        query.put("starttime", starttime);
        query.put("endtime", endtime);

        model.addAttribute("query", query);
        model.addAttribute("lsMember", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,sb.toString()));

        return "member/index";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String update(Integer id, String action, @RequestParam(required = false) String value) {
        id = ConvertUtils.toInteger(id);
        action = ConvertUtils.toString(action);
        value = ConvertUtils.toString(value);

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
                case "lock":
                    memberService.lockMember(id);
                    break;
                case "auth":
                    memberService.verifyAuthentication(id);
                    break;
                case "password":
                    memberService.updateMemberPassword(id,"sysupdate",value,value);
                    break;
                case "add_inviter":
                    memberService.addInviteCodeCount(id,Integer.valueOf(value));
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

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String detail(Integer id, Model model) {
        id = ConvertUtils.toInteger(id);
        Member member =null;
        MemberBankInfo bank = null;
        MemberInfo info = null;
        try {
            if (id > 0) {
                member = memberService.getMember(id);
                info = memberService.getMemberInfo(id);
                bank = memberService.getMemberBankInfoByMember(id);
            }
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        if (member == null) {
            member = new Member();
            member.setId(0);

            info = new MemberInfo();
        }
        if(bank == null)
        {
            bank = new MemberBankInfo();
        }

        model.addAttribute("bank",bank);
        model.addAttribute("info", info);
        model.addAttribute("member", member);
        return "member/detail";
    }
}

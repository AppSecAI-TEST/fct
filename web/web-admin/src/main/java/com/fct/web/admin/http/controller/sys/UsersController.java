package com.fct.web.admin.http.controller.sys;

import com.alibaba.dubbo.common.URL;
import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.AjaxUtil;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.member.data.entity.SystemUser;
import com.fct.member.interfaces.MemberService;
import com.fct.member.interfaces.PageResponse;
import com.fct.web.admin.http.controller.BaseController;
import com.fct.web.admin.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/sys/users")
public class UsersController extends BaseController {
    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String q,Integer page, Model model) {

        q = ConvertUtils.toString(q);
        page = ConvertUtils.toPageIndex(page);

        Integer pageSize = 30;
        String pageUrl = "?page=%d";
        if(!StringUtils.isEmpty(q))
        {
            pageUrl +="&q="+ URL.encode(q);
        }
        PageResponse<SystemUser> pageResponse = null;

        try {

            pageResponse = memberService.findSystemUser(q,page,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<SystemUser>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("q", q);

        model.addAttribute("query", query);
        model.addAttribute("lsUser", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,pageUrl));

        return "sys/users/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model) {
        SystemUser user = new SystemUser();
        user.setId(0);

        model.addAttribute("user",user);
        return "sys/users/create";
    }

    @RequestMapping(value = "/lock", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String lockUser(Integer id) {
        id = ConvertUtils.toInteger(id);

        if (id <= 0) {
            return AjaxUtil.alert("id不正确。");
        }
        try {
            memberService.lockSystemUser(id);
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

    @RequestMapping(value = "/save", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String save(String username,String cellphone,String password,Integer roleid) {
        username = ConvertUtils.toString(username);
        cellphone = ConvertUtils.toString(cellphone);
        password = ConvertUtils.toString(password);
        roleid = ConvertUtils.toInteger(roleid);

        try {
            SystemUser user = new SystemUser();
            user.setUserName(username);
            user.setPassword(password);
            user.setRoleId(roleid);
            user.setCellPhone(cellphone);

            memberService.createSystemUser(user);
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

    @RequestMapping(value = "/savepassword", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String savePassword(String oldpassword,String newpassword,String repassword) {

        oldpassword = ConvertUtils.toString(oldpassword);
        newpassword =ConvertUtils.toString(newpassword);
        repassword =ConvertUtils.toString(repassword);

        try {
            memberService.updateSystemUserPassword(currentUser.getUserId(),oldpassword,newpassword,repassword);
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
        return AjaxUtil.reload("修改密码成功。");
    }

    @RequestMapping(value = "/uppwd", method = RequestMethod.GET)
    public String upPassword(Model model) {

        return "sys/users/uppwd";
    }
}

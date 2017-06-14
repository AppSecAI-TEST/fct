package com.fct.web.admin.http.controller;

import com.fct.web.admin.utils.Constants;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jon on 2017/6/9.
 */
public class BaseController {

    @ModelAttribute
    public void init(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        /*UserItem user = (UserItem)request.getSession().getAttribute(Global.SessionKey_LoginUser);
        if(user == null) {
            response.sendRedirect(request.getContextPath() + "/account/login"); // 跳到登录页面
            return;
        }

        if(!user.isStudent()) {
            response.sendRedirect(request.getContextPath() + "/deny"); // 跳到没有权限页面
            return;
        }*/

        Constants constants =  new Constants();

        model.addAttribute("pub",constants);
    }

}

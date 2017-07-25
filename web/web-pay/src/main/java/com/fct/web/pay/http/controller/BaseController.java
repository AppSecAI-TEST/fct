package com.fct.web.pay.http.controller;

import com.fct.core.utils.CookieUtil;
import com.fct.member.data.entity.MemberLogin;
import com.fct.web.pay.config.FctConfig;
import com.fct.web.pay.http.cache.CacheManager;
import com.fct.web.pay.utils.CryptAES;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by jon on 2017/6/9.
 */
public class BaseController {

    @Autowired
    private CacheManager cacheManager;

    public MemberLogin currentUser;

    @Autowired
    protected FctConfig fctConfig;

    @ModelAttribute
    public void init(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        //获取cookie

        initUser(request,response);

        if(currentUser == null) {
            currentUser = new MemberLogin();
        }
        /*currentUser.setMemberId(5);
        currentUser.setCellPhone("18616311580");
        currentUser.setOpenId("oDYYI1WACqCEH5qB_DCxsPqOBbBs");*/

        model.addAttribute("pub",fctConfig);
        model.addAttribute("currentUser",currentUser);
    }

    void initUser(HttpServletRequest request, HttpServletResponse response)throws Exception
    {
        String token = CookieUtil.getCookieByName(request, "fct_auth");
        String returnUrl = request.getHeader("Referer");
        returnUrl = !StringUtils.isEmpty(returnUrl) ? "?redirecturl="+returnUrl : "";
        if(StringUtils.isEmpty(token))
        {
            response.sendRedirect(fctConfig.getUrl() + "/login"+returnUrl); // 跳到登录页面
            return;
        }
        currentUser = cacheManager.getCacheMemberLogin(CryptAES.AES_Decrypt("7ZaDZOl4mNujVkZN", URLDecoder.decode(token)));
        if (currentUser == null) {
            response.sendRedirect(fctConfig.getUrl() + "/login"+returnUrl); // 跳到登录页面
            return;
        }
    }

    protected String errorPage(String msg)
    {
        try {
            return "redirect:" + fctConfig.getUrl() + "/error?msg=" + URLEncoder.encode(msg, "utf-8");
        }
        catch (Exception exp)
        {
            exp.printStackTrace();
        }
        return "";
    }

}

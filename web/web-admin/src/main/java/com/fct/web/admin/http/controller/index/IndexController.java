package com.fct.web.admin.http.controller.index;

import com.fct.web.admin.http.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by nick on 2017/6/24.
 */
@Controller
public class IndexController extends BaseController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(){

        if(currentUser != null && currentUser.getUserId()>0)
        {
            return "redirect:/order";
        }
        return "redirect:/login";
    }
}

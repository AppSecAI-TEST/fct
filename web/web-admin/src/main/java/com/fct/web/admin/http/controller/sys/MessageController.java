package com.fct.web.admin.http.controller.sys;

import com.alibaba.dubbo.common.URL;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.member.data.entity.SystemUser;
import com.fct.message.data.entity.MessageQueue;
import com.fct.message.interfaces.MessageService;
import com.fct.message.interfaces.PageResponse;
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
@RequestMapping(value = "/sys/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String q,String typeid,Integer status,Integer page, Model model) {

        q = ConvertUtils.toString(q);
        typeid = ConvertUtils.toString(typeid);
        status = ConvertUtils.toInteger(status);
        page = ConvertUtils.toPageIndex(page);

        Integer pageSize = 30;
        String pageUrl = "?page=%d";
        if(!StringUtils.isEmpty(q))
        {
            pageUrl +="&q="+ URL.encode(q);
        }
        if(!StringUtils.isEmpty(typeid))
        {
            pageUrl += "&typeid="+typeid;
        }
        if(status>-1)
        {
            pageUrl += "&status="+status;
        }
        PageResponse<MessageQueue> pageResponse = null;

        try {
            pageResponse = messageService.findAll(typeid,status,q,page,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
            pageResponse = new PageResponse<MessageQueue>();
        }

        Map<String,Object> query = new HashMap<>();
        query.put("q", q);

        model.addAttribute("query", query);
        model.addAttribute("lsMessage", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,pageUrl));

        return "sys/message/index";
    }
}

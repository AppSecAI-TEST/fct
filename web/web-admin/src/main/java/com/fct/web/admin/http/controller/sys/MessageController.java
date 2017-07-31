package com.fct.web.admin.http.controller.sys;

import com.alibaba.dubbo.common.URL;
import com.fct.core.exceptions.Exceptions;
import com.fct.core.utils.AjaxUtil;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.PageUtil;
import com.fct.message.data.entity.MessageQueue;
import com.fct.message.interfaces.MessageService;
import com.fct.message.interfaces.PageResponse;
import com.fct.web.admin.http.controller.BaseController;
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

@Controller
@RequestMapping(value = "/sys/message")
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(String q,String typeid,Integer status,Integer page, Model model) {

        q = ConvertUtils.toString(q);
        typeid = ConvertUtils.toString(typeid);
        status = ConvertUtils.toInteger(status,-2);
        page = ConvertUtils.toPageIndex(page);

        Integer pageSize = 30;
        StringBuilder sb = new StringBuilder();
        sb.append("?page=%d");
        if(!StringUtils.isEmpty(q))
        {
            sb.append("&q="+ URL.encode(q));
        }
        if(!StringUtils.isEmpty(typeid))
        {
            sb.append("&typeid="+typeid);
        }
        if(status>-1)
        {
            sb.append("&status="+status);
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
        query.put("typeid", typeid);
        query.put("status", status);

        model.addAttribute("query", query);
        model.addAttribute("lsMessage", pageResponse.getElements());
        model.addAttribute("pageHtml", PageUtil.getPager(pageResponse.getTotalCount(),page,
                pageSize,sb.toString()));

        return "sys/message/index";
    }


    @RequestMapping(value = "/upstatus", method = RequestMethod.POST,produces="application/json;charset=UTF-8")
    @ResponseBody
    public String upStatus(Integer id,String action)
    {
        id = ConvertUtils.toInteger(id);
        action = ConvertUtils.toString(action);

        try {
            switch (action)
            {
                case "resume":
                    messageService.resume(id);
                    break;
            }
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

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public String detail(Integer id,Model model)
    {
        id = ConvertUtils.toInteger(id);
        MessageQueue messageQueue = null;
        try
        {
            messageQueue = messageService.getMessage(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        if(messageQueue == null)
        {
            messageQueue = new MessageQueue();
        }
        model.addAttribute("message", messageQueue);
        return "/sys/message/detail";
    }
}

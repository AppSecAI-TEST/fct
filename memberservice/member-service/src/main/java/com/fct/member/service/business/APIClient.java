package com.fct.member.service.business;

import com.fct.message.interfaces.MessageService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jon on 2017/5/5.
 */
public class APIClient {
    @Autowired
    public static MessageService messageService;
}

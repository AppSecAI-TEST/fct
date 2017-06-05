package com.fct.web.admin.utils;

import com.fct.mall.interfaces.MallService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jon on 2017/6/4.
 */
public class Constants {

    public static final Logger logger = LoggerFactory.getLogger("EX");

    @Autowired
    public MallService mallService;
}

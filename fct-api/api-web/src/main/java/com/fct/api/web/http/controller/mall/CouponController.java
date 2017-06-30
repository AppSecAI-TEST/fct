package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
import com.fct.mall.interfaces.MallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by z on 17-6-30.
 */
@RestController
@RequestMapping(value = "coupons")
public class CouponController extends BaseController {

    @Autowired
    private MallService mallService;
}

package com.fct.api.web.http.controller.mall;

import com.fct.api.web.http.controller.BaseController;
import com.fct.common.interfaces.CommonService;
import com.fct.common.interfaces.WeChatShareResponse;
import com.fct.core.utils.ConvertUtils;
import com.fct.core.utils.ReturnValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/mall/share")
public class ShareContrller extends BaseController {

    @Autowired
    private CommonService commonService;

    @RequestMapping(value = "wechat", method = RequestMethod.GET)
    public ReturnValue<WeChatShareResponse> weChatShare(String share_url) {

        share_url = ConvertUtils.toString(share_url);
        if (StringUtils.isEmpty(share_url))
            return new ReturnValue<>(404, "分享url不存在");

        WeChatShareResponse share = commonService.jsShare(share_url, true);

        ReturnValue<WeChatShareResponse> response = new ReturnValue<>();
        response.setData(share);

        return response;
    }

}

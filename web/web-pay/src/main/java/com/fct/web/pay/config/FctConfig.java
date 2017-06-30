package com.fct.web.pay.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class FctConfig {

    @Value("${fct.domain.url}")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Value("${fct.domain.payurl}")
    private String payUrl;

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    @Value("${fct.domain.imageurl}")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Value("${fct.domain.staticurl}")
    private String staticUrl;

    public String getStaticUrl() {
        return staticUrl;
    }

    public void setStaticUrl(String staticUrl) {
        this.staticUrl = staticUrl;
    }

    public String thumbnail(String path)
    {
        if(StringUtils.isEmpty(path))
        {
            path = "";
        }
        return String.format("%s%s@200w.jpg",imageUrl,path);
    }

    public String staticPath (String  path) {
        if(StringUtils.isEmpty(path))
        {
            path = "";
        }
        return staticUrl + path;
    }

    public String imagesPath (String  path) {
        if(StringUtils.isEmpty(path))
        {
            path = "";
        }
        return imageUrl + path;
    }
}

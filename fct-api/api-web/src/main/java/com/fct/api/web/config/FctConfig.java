package com.fct.api.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by z on 17-6-28.
 */
@Configuration
public class FctConfig {

    @Value("${fct.domain.staticurl}")
    private String staticUrl;

    public void setStaticUrl(String staticUrl)
    {
        this.staticUrl = staticUrl;
    }

    public String getStaticUrl()
    {
        return staticUrl;
    }


    @Value("${fct.domain.imageurl}")
    private String imageUrl;

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    @Value("${fct.domain.url}")
    private String url;
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }


    @Value("${fct.domain.payurl}")
    private String payUrl;

    public void setPayUrl(String payUrl)
    {
        this.payUrl = payUrl;
    }

    public String getPayUrl()
    {
        return payUrl;
    }

    @Value("${fct.domain.videourl}")
    private String videoUrl;

    public void setVideoUrl(String videoUrl)
    {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl()
    {
        return videoUrl;
    }
}

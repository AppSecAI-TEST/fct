package com.fct.api.web.utils;

import com.fct.api.web.config.FctConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by z on 17-7-7.
 */

@Service
public class FctResourceUrl {

    @Autowired
    private FctConfig fctConfig;

    private String getImageUrl(String url)
    {
        if (StringUtils.isEmpty(url))
            return this.defaultNullImage();

        return String.format("%s%s",fctConfig.getImageUrl(),url);
    }

    public String thumbnail(String url, int size)
    {
        if(StringUtils.isEmpty(url))
            return this.defaultNullImage();

        return String.format("%s!%d", this.getImageUrl(url), size);
    }

    /**最小尺寸
     *
     * @param url
     * @return
     */
    public String thumbSmall(String url) {

        return this.thumbnail(url, 120);
    }

    public String thumbMedium(String url) {

        return this.thumbnail(url, 350);
    }

    public String thumbLarge(String url) {

        return this.thumbnail(url, 750);
    }

    public List<String> getMutilImageUrl(String url, String size)
    {
        if(StringUtils.isEmpty(url))
        {
            return new ArrayList<>();
        }
        String[] arrUrl = url.split(",");
        List<String> ls = new ArrayList<>();
        for (String img:arrUrl) {
            switch (size) {
                case "small":
                    ls.add(this.thumbSmall(img));
                    break;
                case "medium":
                    ls.add(this.thumbSmall(img));
                    break;
                case "large":
                    ls.add(this.thumbSmall(img));
                    break;
            }
        }

        return ls;
    }

    public String defaultNullImage()
    {
        return this.getImageUrl("/images/default-null.png");
    }

    public String getAvatarUrl(String url) {
        if (StringUtils.isEmpty(url))
            return this.getImageUrl("/static/img/head.jpg");

        if (url.indexOf("//") > 0 || url.indexOf("http") > 0) {
            return url;
        }

        return this.thumbSmall(url);
    }
}

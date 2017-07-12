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

    public String getImageUrl(String url)
    {
        if (StringUtils.isEmpty(url))
            return this.defaultNullImage();

        return String.format("%s%s",fctConfig.getImageUrl(),url);
    }

    public List<String> getMutilImageUrl(String url)
    {
        if(StringUtils.isEmpty(url))
        {
            return new ArrayList<>();
        }
        String[] arrUrl = url.split(",");
        List<String> ls = new ArrayList<>();
        for (String img:arrUrl) {

            if (StringUtils.isEmpty(url))
                ls.add(this.defaultNullImage());

            else
                ls.add(String.format("%s%s", fctConfig.getImageUrl(), img));
        }

        return ls;
    }

    private String defaultNullImage()
    {
        return String.format("%s%s","","/images/default-null.png");
    }
}

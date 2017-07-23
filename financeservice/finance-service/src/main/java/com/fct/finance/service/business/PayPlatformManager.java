package com.fct.finance.service.business;

import com.fct.finance.data.entity.PayPlatform;
import com.fct.finance.data.entity.RechargeRecord;
import com.fct.finance.data.repository.PayPlatformRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jon on 2017/4/21.
 */
@Service
public class PayPlatformManager {
    @Autowired
    private PayPlatformRepository payPlatformRepository;

    @Autowired
    private JdbcTemplate jt;

    public List<PayPlatform> findAll(String type,String website,Integer status)
    {
        StringBuilder sb = new StringBuilder();
        List<Object> param = new ArrayList<>();
        sb.append("SELECT * FROM PayPlatform WHERE 1=1 ");
        if(status>-1)
        {
            sb.append(" and status="+status);
        }
        if(!StringUtils.isEmpty(type))
        {
            sb.append(" and type=?");
            param.add(type);
        }
        if(!StringUtils.isEmpty(website))
        {
            sb.append(" and website=?");
            param.add(type);
        }

        sb.append("order by sortIndex asc");

        return jt.query(sb.toString(), param.toArray(),
                new BeanPropertyRowMapper<PayPlatform>(PayPlatform.class));
    }
}

package com.fct.member.service.business;

import com.fct.core.utils.DateUtils;
import com.fct.member.data.entity.*;
import com.fct.member.data.repository.SysUserLoginRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class SysUserLoginManager {

    @Autowired
    private SysUserLoginRepository sysUserLoginRepository;

    @Autowired
    private SystemUserManager systemUserManager;


    public SysUserLogin login(String userName, String password, String ip, Integer expireHour)
    {
        if(StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("用户名为空。");
        }
        //普通登陆，用户名+密码
        SystemUser user = systemUserManager.login(userName,password);
        if(user == null)
        {
            throw new IllegalArgumentException("用户户或密码错误。");
        }

        SysUserLogin login = new SysUserLogin();
        login.setCellPhone(user.getCellPhone());
        login.setUserId(user.getId());
        login.setUserName(user.getUserName());
        login.setExpiredTime(DateUtils.addHour(new Date(),expireHour));
        login.setIp(ip);
        login.setToken(UUID.randomUUID().toString());
        login.setCreateTime(new Date());

        sysUserLoginRepository.save(login);

        return login;
    }

    public SysUserLogin findByToken(String token)
    {
        if(StringUtils.isEmpty(token))
        {
            throw new IllegalArgumentException("token为空。");
        }
        SysUserLogin login =  sysUserLoginRepository.findOne(token);
        if(DateUtils.compareDate(login.getExpiredTime(),new Date())<0)
        {
            return  null;
        }
        return login;
    }

    public void logOut(String token)
    {
        if(StringUtils.isEmpty(token))
        {
            throw new IllegalArgumentException("token为空。");
        }
        SysUserLogin login = sysUserLoginRepository.findOne(token);
        login.setExpiredTime(DateUtils.addDay(new Date(),-1));
        sysUserLoginRepository.save(login);
    }
}

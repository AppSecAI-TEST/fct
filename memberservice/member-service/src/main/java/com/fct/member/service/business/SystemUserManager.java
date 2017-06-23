package com.fct.member.service.business;

import com.fct.core.utils.PageUtil;
import com.fct.core.utils.StringHelper;
import com.fct.member.data.entity.SystemUser;
import com.fct.member.data.repository.SystemUserRepository;
import com.fct.member.interfaces.PageResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/8.
 * jon love nancy forever
 */
@Service
public class SystemUserManager {

    @Autowired
    private SystemUserRepository systemUserRepository;

    @Autowired
    private JdbcTemplate jt;

    public void create(SystemUser user)
    {
        if(StringUtils.isEmpty(user.getCellPhone()))
        {
            throw new IllegalArgumentException("手机号码为空。");
        }
        if(StringUtils.isEmpty(user.getUserName()))
        {
            throw new IllegalArgumentException("用户名为空。");
        }
        if(StringUtils.isEmpty(user.getPassword()))
        {
            throw new IllegalArgumentException("密码为空。");
        }
        if(systemUserRepository.countByUserName(user.getUserName())>0)
        {
            throw new IllegalArgumentException("用户名存在。");
        }
        user.setPassword(StringHelper.md5(user.getPassword()));
        user.setCreateTime(new Date());
        user.setLocked(0);
        user.setRoleId(0);
        systemUserRepository.save(user);
    }

    public void updatePassword(Integer userId,String oldPassword,String newPassword,String reNewPassword)
    {
        if(userId<=0)
        {
            throw new IllegalArgumentException("用户Id为空。");
        }
        if(StringUtils.isEmpty(oldPassword))
        {
            throw new IllegalArgumentException("旧密码为空.");
        }
        if(StringUtils.isEmpty(newPassword))
        {
            throw new IllegalArgumentException("新密码为空");
        }
        if(StringUtils.isEmpty(reNewPassword))
        {
            throw new IllegalArgumentException("重复密码为空");
        }
        SystemUser user = systemUserRepository.findOne(userId);
        if(user == null)
        {
            throw new IllegalArgumentException("管理员不存在。");
        }
        if(user.getPassword() != StringHelper.md5(oldPassword))
        {
            throw new IllegalArgumentException("旧密码不正确。");
        }
        if(newPassword!=reNewPassword)
        {
            throw new IllegalArgumentException("新密码与重复密码不一致。");
        }

        user.setPassword(StringHelper.md5(newPassword));
        systemUserRepository.saveAndFlush(user);

    }

    public SystemUser login(String userName,String password)
    {
        if(StringUtils.isEmpty(userName))
        {
            throw new IllegalArgumentException("用户名为空.");
        }
        if(StringUtils.isEmpty(password))
        {
            throw new IllegalArgumentException("密码为空");
        }
        return systemUserRepository.login(userName, StringHelper.md5(password));
    }

    public void lock(Integer userId)
    {
        if(userId<=0)
        {
            throw new IllegalArgumentException("用户Id为空");
        }
        systemUserRepository.lock(userId);
    }

    private String getCondition(String username,List<Object> param) {

        String condition = "";
        if (!StringUtils.isEmpty(username))
        {
            condition += " AND UserName=?";
            param.add(username);
        }
        return condition;
    }

    public PageResponse<SystemUser> findAll(String userName, Integer pageIndex, Integer pageSize)
    {
        List<Object> param = new ArrayList<>();

        String table="SystemUser";
        String field ="*";
        String orderBy = "ID asc";
        String condition= getCondition(userName,param);

        String sql = "SELECT Count(0) FROM SystemUser WHERE 1=1 "+condition;
        Integer count =  jt.queryForObject(sql,param.toArray(),Integer.class);

        sql = PageUtil.getPageSQL(table,field,condition,orderBy,pageIndex,pageSize);

        List<SystemUser> ls = jt.query(sql, param.toArray(), new BeanPropertyRowMapper<SystemUser>(SystemUser.class));

        int end = pageIndex+1;
        Boolean hasmore = true;
        if(pageIndex*pageSize >= count)
        {
            end = pageIndex;
            hasmore = false;
        }
        PageResponse<SystemUser> p = new PageResponse<>();
        p.setTotalCount(count);
        p.setCurrent(end);
        p.setElements(ls);
        p.setHasMore(hasmore);

        return p;

    }
}

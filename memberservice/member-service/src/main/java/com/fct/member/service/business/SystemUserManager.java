package com.fct.member.service.business;

import com.fct.common.utils.StringHelper;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.SystemUser;
import com.fct.member.data.repository.SystemUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

    public void create(SystemUser user)
    {
        if(systemUserRepository.countByUserName(user.getUserName())>0)
        {
            throw new IllegalArgumentException("用户名存在。");
        }
        user.setCreateTime(new Date());
        systemUserRepository.save(user);
    }

    public void updatePassword(Integer userId,String oldPassword,String newPassword,String reNewPassword)
    {
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
        return systemUserRepository.login(userName, StringHelper.md5(password));
    }

    public void lock(Integer userId)
    {
        systemUserRepository.lock(userId);
    }

    public Page<SystemUser> findAll(String userName, Integer pageIndex, Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<SystemUser> spec = new Specification<SystemUser>() {
            @Override
            public Predicate toPredicate(Root<SystemUser> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(userName)) {
                    predicates.add(cb.equal(root.get("userName"), userName));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return systemUserRepository.findAll(spec,pageable);

    }
}

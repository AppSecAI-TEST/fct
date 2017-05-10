package com.fct.member.data.repository;

import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.SystemUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jon on 2017/5/8.
 */
public interface SystemUserRepository extends JpaRepository<SystemUser, Integer> {

    int countByUserName(String userName);

    @Query(nativeQuery = true, value = "SELECT * FROM SystemUser WHERE userName=?1 AND Password=?2 AND locked=0")
    SystemUser login(String userName,String password);

    @Query(nativeQuery = true, value = "UPDATE SystemUser SET locked=1-locked WHERE id=?1")
    void lock(Integer userId);

    Page<SystemUser> findAll(Specification<SystemUser> spec, Pageable pageable);  //分页按条件查询
}

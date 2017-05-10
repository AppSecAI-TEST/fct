package com.fct.member.data.repository;

import com.fct.member.data.entity.InviteCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/8.
 */
public interface InviteCodeRepository extends JpaRepository<InviteCode, Integer> {

    InviteCode findByCode(String code);

    Page<InviteCode> findAll(Specification<InviteCode> spec, Pageable pageable);  //分页按条件查询
}

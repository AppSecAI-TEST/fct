package com.fct.member.data.repository;

import com.fct.member.data.entity.MemberStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/6.
 */
public interface MemberStoreRepository extends JpaRepository<MemberStore, Integer> {

    MemberStore findByMemberId(Integer memberId);

    Page<MemberStore> findAll(Specification<MemberStore> spec, Pageable pageable);  //分页按条件查询
}

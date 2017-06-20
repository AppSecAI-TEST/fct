package com.fct.member.data.repository;

import com.fct.member.data.entity.MemberStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jon on 2017/5/6.
 */
public interface MemberStoreRepository extends JpaRepository<MemberStore, Integer> {

    MemberStore findByMemberId(Integer memberId);

    @Modifying
    @Query("UPDATE MemberStore SET status=1-status WHERE id=?1")
    void updateStatus(Integer id);
}

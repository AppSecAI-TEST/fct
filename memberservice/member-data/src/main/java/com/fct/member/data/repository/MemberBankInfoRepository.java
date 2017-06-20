package com.fct.member.data.repository;

import com.fct.member.data.entity.MemberBankInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by jon on 2017/5/8.
 */
public interface MemberBankInfoRepository extends JpaRepository<MemberBankInfo, Integer> {

    List<MemberBankInfo> findByMemberId(Integer memberId);

    @Query(nativeQuery = true, value = "SELECT * FROM MemberBankInfo WHERE MemberId=?1 limit 1")
    MemberBankInfo findOneByMemberId(Integer memberId);
}

package com.fct.member.data.repository;

import com.fct.member.data.entity.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by jon on 2017/5/8.
 */
public interface MemberAddressRepository extends JpaRepository<MemberAddress, Integer> {

    List<MemberAddress> findByMemberId(Integer memberId);

    @Query(nativeQuery = true, value = "SELECT * FROM MemberAddress WHERE MemberId=?1 AND isDefault=1 limit 1")
    MemberAddress findDefault(Integer memberId);
}

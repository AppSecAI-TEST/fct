package com.fct.member.data.repository;

import com.fct.member.data.entity.MemberAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by jon on 2017/5/8.
 */
public interface MemberAddressRepository extends JpaRepository<MemberAddress, Integer> {

    List<MemberAddress> findByMemberId(Integer memberId);
}

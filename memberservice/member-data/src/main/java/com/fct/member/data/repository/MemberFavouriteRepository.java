package com.fct.member.data.repository;

import com.fct.member.data.entity.MemberFavourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface MemberFavouriteRepository extends JpaRepository<MemberFavourite, Integer> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM MemberFavourite WHERE MemberId=?1 and favtype=?2 and relatedId=?3")
    void deleteByMember(Integer memberId,Integer favType,Integer relatedId);

    @Query(nativeQuery = true, value = "SELECT count(0) FROM MemberFavourite WHERE MemberId=?1 and favtype=?2 and relatedId=?3")
    int getCountByMember(Integer memberId,Integer favType,Integer relatedId);

    @Query(nativeQuery = true, value = "SELECT count(0) FROM MemberFavourite WHERE favtype=?1 and relatedId=?2")
    int getCountByType(Integer favType,Integer relatedId);
}

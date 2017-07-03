package com.fct.member.data.repository;

import com.fct.member.data.entity.MemberFavourite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberFavouriteRepository extends JpaRepository<MemberFavourite, Integer> {

    void deleteByMemberIdAndId(Integer memberId,Integer id);

    int countByMemberIdAndFavTypeAndRelatedId(Integer memberId,Integer favType,Integer relatedId);

    int countByFavTypeAndRelatedId(Integer favType,Integer relatedId);
}

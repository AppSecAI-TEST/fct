package com.fct.member.data.repository;

import com.fct.member.data.entity.MemberAuth;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/8.
 */
public interface MemberAuthRepository extends JpaRepository<MemberAuth, Integer> {

    MemberAuth findByMemberIdAndPlatform(Integer memberId,String platform);

}

package com.fct.member.data.repository;

import com.fct.member.data.entity.MemberAuth;
import com.fct.member.data.entity.MemberBankInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jon on 2017/5/8.
 */
public interface MemberAuthRepository extends JpaRepository<MemberAuth, Integer> {

    MemberAuth findByMemberIdAndPlatform(Integer memberId,String platform);

    @Query(nativeQuery = true, value = "SELECT * FROM MemberAuth WHERE openId=?1 and platform=?2 limit 1")
    MemberAuth findOneByOpenId(String openId,String platform);

}

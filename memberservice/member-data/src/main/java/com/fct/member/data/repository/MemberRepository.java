package com.fct.member.data.repository;

import com.fct.member.data.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * Created by jon on 2017/5/2.
 */
public interface MemberRepository extends JpaRepository<Member, Integer> {

    int countByCellPhone(String cellphone);

    int countByUserName(String userName);

    @Query(nativeQuery = true, value = "SELECT * FROM Member WHERE CellPhone=?1 AND Password=?2 AND locked=0")
    Member login(String cellPhone,String password);

    Member findByCellPhone(String cellphone);

    @Transactional
    @Modifying
    @Query("UPDATE Member SET password=?2 WHERE cellPhone=?1")
    void updatePassword(String cellphone,String password);

    @Transactional
    @Modifying
    @Query("UPDATE Member SET locked=1-locked WHERE id=?1")
    void lock(Integer memberId);

    @Transactional
    @Modifying
    @Query("UPDATE Member SET canInviteCount=canInviteCount+?2 WHERE id=?1")
    void addInviteCount(Integer memberId,Integer count);

    @Modifying
    @Query("UPDATE Member SET authStatus=1-authStatus WHERE id=?1")
    void verifyAuthStatus(Integer memberId);
}

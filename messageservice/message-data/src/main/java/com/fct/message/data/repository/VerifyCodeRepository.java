package com.fct.message.data.repository;

import com.fct.message.data.entity.VerifyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by jon on 2017/5/6.
 */
public interface VerifyCodeRepository extends JpaRepository<VerifyCode, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM VerifyCode WHERE SessionId=?1 and cellphone=?2 AND expireTime>=3 limit 1")
    VerifyCode getCode(String sessionId,String cellphone,String expireTime);

    @Query(nativeQuery = true, value = "SELECT count(0) FROM VerifyCode WHERE SessionId=?1 and cellphone=?2 AND Code=?3 AND expireTime>=4 limit 1")
    int check(String sessionId,String cellPhone,String code,String expireTime);

    @Query(nativeQuery = true, value = "SELECT Count(0) FROM VerifyCode WHERE CellPhone=?1 AND CreateTime>=?2 AND CreateTime<?3")
    int getCountByToday(String cellphone,String beginTime,String endTime);

    @Query(nativeQuery = true, value = "SELECT Count(0) FROM VerifyCode WHERE CellPhone=?2 AND SessdionId=?1")
    int getCountBySession(String sessionId,String cellphone);

}

package com.fct.member.data.repository;

import com.fct.member.data.entity.MemberLogin;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/6.
 */
public interface MemberLoginRepository extends JpaRepository<MemberLogin, String> {


}

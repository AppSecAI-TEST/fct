package com.fct.member.data.repository;

import com.fct.member.data.entity.SysUserLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SysUserLoginRepository extends JpaRepository<SysUserLogin, String> {
}

package com.fct.member.data.repository;

import com.fct.member.data.entity.MemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jon on 2017/5/6.
 */
public interface MemberInfoRepository extends JpaRepository<MemberInfo, Integer> {


}

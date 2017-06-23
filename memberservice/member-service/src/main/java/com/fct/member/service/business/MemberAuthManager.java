package com.fct.member.service.business;

import com.fct.member.data.entity.MemberAuth;
import com.fct.member.data.repository.MemberAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jon on 2017/5/8.
 */
@Service
public class MemberAuthManager {

    @Autowired
    MemberAuthRepository memberAuthRepository;

    public MemberAuth save(MemberAuth auth)
    {

        return memberAuthRepository.saveAndFlush(auth);
    }
}

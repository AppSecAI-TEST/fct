package com.fct.member.service.business;

import com.fct.member.data.entity.MemberAuth;
import com.fct.member.data.repository.MemberAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jon on 2017/5/8.
 */
public class MemberAuthManager {

    // 将自身的实例对象设置为一个属性,并加上Static和final修饰符
    private static final MemberAuthManager instance = new MemberAuthManager();

    // 静态方法返回该类的实例
    public static MemberAuthManager getInstance() {
        return instance;
    }

    @Autowired
    MemberAuthRepository memberAuthRepository;

    public MemberAuth save(MemberAuth auth)
    {
        return memberAuthRepository.saveAndFlush(auth);
    }
}

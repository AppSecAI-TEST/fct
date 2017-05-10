package com.fct.member.service.business;

import com.fct.member.data.entity.MemberAddress;
import com.fct.member.data.repository.MemberAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jon on 2017/5/7.
 */
@Service
public class MemberAddressManager {
    // 将自身的实例对象设置为一个属性,并加上Static和final修饰符
    private static final MemberAddressManager instance = new MemberAddressManager();

    // 静态方法返回该类的实例
    public static MemberAddressManager getInstance() {
        return instance;
    }

    @Autowired
    private MemberAddressRepository memberAddressRepository;

    public void save(MemberAddress adress)
    {
        memberAddressRepository.saveAndFlush(adress);
    }

    public MemberAddress findById(Integer id)
    {
        return memberAddressRepository.findOne(id);
    }

    public List<MemberAddress> findAll(Integer memberId)
    {
        return memberAddressRepository.findByMemberId(memberId);
    }
}

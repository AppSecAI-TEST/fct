package com.fct.member.service.business;

import com.fct.member.data.entity.MemberAddress;
import com.fct.member.data.repository.MemberAddressRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/7.
 */
@Service
public class MemberAddressManager {

    @Autowired
    private MemberAddressRepository memberAddressRepository;

    public void save(MemberAddress address)
    {
        if(address.getMemberId() <=0)
        {
            throw new IllegalArgumentException("用户为空");
        }
        if(StringUtils.isEmpty(address.getName()))
        {
            throw new IllegalArgumentException("姓名为空");
        }
        if(StringUtils.isEmpty(address.getAddress()))
        {
            throw new IllegalArgumentException("地址为空");
        }
        if(StringUtils.isEmpty(address.getCellPhone()))
        {
            throw new IllegalArgumentException("联系电话为空");
        }
        if(StringUtils.isEmpty(address.getProvince()))
        {
            throw new IllegalArgumentException("省份为空");
        }
        if(StringUtils.isEmpty(address.getCityId()))
        {
            throw new IllegalArgumentException("城市为空");
        }
        if(StringUtils.isEmpty(address.getTownId()))
        {
            throw new IllegalArgumentException("区域为空");
        }
        if(address.getId() == null || address.getId()<=0) {
            address.setCreateTime(new Date());
        }
        memberAddressRepository.save(address);
    }

    public MemberAddress findById(Integer id)
    {
        if(id<=0)
        {
            throw new IllegalArgumentException("id 为空");
        }
        return memberAddressRepository.findOne(id);
    }

    public List<MemberAddress> findAll(Integer memberId)
    {
        if(memberId<=0)
        {
            throw new IllegalArgumentException("会员为空");
        }
        return memberAddressRepository.findByMemberId(memberId);
    }
}

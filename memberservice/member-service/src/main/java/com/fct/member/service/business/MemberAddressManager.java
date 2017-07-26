package com.fct.member.service.business;

import com.fct.member.data.entity.MemberAddress;
import com.fct.member.data.repository.MemberAddressRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/7.
 */
@Service
public class MemberAddressManager {

    @Autowired
    private MemberAddressRepository memberAddressRepository;

    @Autowired
    private JdbcTemplate jt;

    @Transactional
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

            if(memberAddressRepository.countByMemberId(address.getMemberId())>=10)
            {
                throw new IllegalArgumentException("您的收货地址太多啦..");
            }
        }
        if(address.getIsDefault() ==1)
        {
            String sql = String.format("UPDATE MemberAddress set isDefault=0 WHERE MemberId=%d",address.getMemberId());
            jt.update(sql);
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

    public MemberAddress findByDefault(Integer memberId)
    {
        return memberAddressRepository.findDefault(memberId);
    }

    @Transactional
    public void setDefault(Integer id,Integer memberId)
    {
        if(memberId<=0 || id<=0)
        {
            throw new IllegalArgumentException("id 为空");
        }
        //将用户原来的地址默认先取消
        String sql = String.format("UPDATE MemberAddress set isDefault=0 WHERE MemberId=%d",memberId);
        jt.update(sql);

        sql = String.format("UPDATE MemberAddress set isDefault=1 WHERE id=%d AND MemberId=%d",id,memberId);
        jt.update(sql);
    }

    public void delete(Integer id,Integer memberId)
    {
        memberAddressRepository.delete(id,memberId);
    }
}

package com.fct.member.service.business;

import com.fct.member.data.entity.MemberAuth;
import com.fct.member.data.repository.MemberAuthRepository;
import org.apache.commons.lang3.StringUtils;
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

    public String getOpenId(Integer memberId,String platform) {

        if(memberId <=0 || StringUtils.isEmpty(platform))
        {
            return "";
        }
        MemberAuth auth = memberAuthRepository.findByMemberIdAndPlatform(memberId,platform);
        if(auth != null)
            return auth.getOpenId();

        return "";
    }
}

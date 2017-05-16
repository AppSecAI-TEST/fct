package com.fct.member.service;


import com.fct.member.data.entity.*;
import com.fct.member.service.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by jon on 2017/5/5.
 */
@Service(value = "memberService")
public class MemberServiceImpl implements com.fct.member.interfaces.MemberService{

    @Autowired
    private MemberManager memberManager;

    @Autowired
    private MemberLoginManager memberLoginManager;

    @Autowired
    private MemberBankInfoManager memberBankInfoManager;

    @Autowired
    private MemberInfoManager memberInfoManager;

    @Autowired
    private InviteCodeManager inviteCodeManager;

    @Autowired
    private MemberAddressManager memberAddressManager;

    @Autowired
    private MemberAuthManager memberAuthManager;

    @Autowired
    private MemberStoreManager memberStoreManager;

    @Autowired
    private SystemUserManager systemUserManager;

    /*注册会员*/
//    @Transactional
    public Member registerMember(String cellPhone, String userName, String password)
    {
        System.out.println("in method>>");
        return memberManager.register(cellPhone,userName,password);
    }

    /*普通登录和快捷登录*/
    public MemberLogin loginMember(String cellPhone, String password, String ip,Integer expireDay)
    {
        return memberLoginManager.login(cellPhone,password,ip,expireDay);
    }

    public Member getMember(Integer memberId)
    {
        System.out.println("getMember in");
        return memberManager.findById(memberId);
    }

    /*修改密码*/
    public void updateMemberPassword(Integer memberId,String oldPassword,String newPassword,String reNewPassword)
    {
        memberManager.updatePassword(memberId,oldPassword,newPassword,reNewPassword);
    }

    public void forgetPassword(String cellPhone,String password)
    {
        memberManager.forgetPassword(cellPhone,password);
    }

    public void lockMember(Integer memberId)
    {
        memberManager.lock(memberId);
    }

    public Page<Member> findMember(String cellPhone,String beginTime,String endTime, Integer pageIndex,Integer pageSize)
    {
        return memberManager.findAll(cellPhone,beginTime,endTime,pageIndex,pageSize);
    }

    public void updateMemberInfo(MemberInfo info)
    {
        memberInfoManager.save(info);
    }

    public MemberInfo getMemberInfo(Integer memberId)
    {
        return memberInfoManager.findById(memberId);
    }

    public void saveMemberAddress(MemberAddress address)
    {
        memberAddressManager.save(address);
    }

    public MemberAddress getMemberAddress(Integer id)
    {
        return memberAddressManager.findById(id);
    }

    public List<MemberAddress> findMemberAddress(Integer memberId)
    {
        return memberAddressManager.findAll(memberId);
    }

    public void authenticationMember(Integer memberId,String name,String identityCardNo,String identityCardImg,
                         String bankName,String bankAccount)
    {
        memberInfoManager.authentication(memberId,name,identityCardNo,identityCardImg,bankName,bankAccount);
    }

    public void verifyAuthentication(Integer memberId)
    {
        memberManager.verifyAuthStatus(memberId);
    }

    public void saveMemberBankInfo(MemberBankInfo bankInfo)
    {
        memberBankInfoManager.save(bankInfo);
    }

    public MemberBankInfo getMemberBankInfo(Integer id)
    {
        return memberBankInfoManager.findById(id);
    }

    public Page<MemberBankInfo> findMemberBankInfo(String cellPhone,String name,Integer status,Integer pageIndex,
                                                   Integer pageSize)
    {
        return memberBankInfoManager.findAll(cellPhone,name,status,pageIndex,pageSize);
    }

    public void createInviteCode(Integer memberId)
    {
        inviteCodeManager.create(memberId);
    }

    public void addInviteCodeCount(Integer memberId,Integer count)
    {
        memberManager.addInviteCount(memberId,count);
    }

    public Page<InviteCode> findInviteCode(Integer ownerId, String ownerCellPhone, int pageIndex, int pageSize)
    {
        return inviteCodeManager.findAll(ownerId,ownerCellPhone,pageIndex,pageSize);
    }

    public MemberStore applyStore(Integer memberId,String inviteCode)
    {
        return memberStoreManager.apply(memberId,inviteCode);
    }

    public Page<MemberStore> findMemberStore(String cellPhone,Integer status,Integer pageIndex,Integer pageSize)
    {
        return memberStoreManager.findAll(cellPhone,status,pageIndex,pageSize);
    }

    public MemberAuth saveMemberAuth(MemberAuth auth)
    {
        return memberAuthManager.save(auth);
    }

    public MemberAuth getMemberAuth(String platform)
    {
        return  null;
    }

    public void createSystemUser(SystemUser user)
    {
        systemUserManager.create(user);
    }

    public SystemUser loginSystemUser(String userName,String password)
    {
        return systemUserManager.login(userName,password);
    }

    public void lockSystemUser(Integer userId)
    {
        systemUserManager.lock(userId);
    }

    public void updateSystemUserPassword(Integer memberId,String oldPassword,String newPassword,String reNewPassword)
    {
        systemUserManager.updatePassword(memberId,oldPassword,newPassword,reNewPassword);
    }

    public Page<SystemUser> findSystemUser(String userName, Integer pageIndex, Integer pageSize)
    {
        return systemUserManager.findAll(userName,pageIndex,pageSize);
    }
}

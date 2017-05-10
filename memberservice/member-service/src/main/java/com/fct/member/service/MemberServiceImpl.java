package com.fct.member.service;


import com.fct.member.data.entity.*;
import com.fct.member.service.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jon on 2017/5/5.
 */
@Service(value = "memberService")
public class MemberServiceImpl implements com.fct.member.interfaces.MemberService{

    @Autowired
    MemberManager memberManager;

    @Autowired
    MemberLoginManager memberLoginManager;
    /*注册会员*/
    public Member registerMember(String cellPhone, String userName, String password)
    {
        return memberManager.register(cellPhone,userName,password);
    }

    /*普通登录和快捷登录*/
    public MemberLogin loginMember(String cellPhone, String password, String ip,Integer expireDay)
    {
        return memberLoginManager.login(cellPhone,password,ip,expireDay);
    }

    public Member getMember(Integer memberId)
    {
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
        MemberInfoManager.getInstance().save(info);
    }

    public MemberInfo getMemberInfo(Integer memberId)
    {
        return MemberInfoManager.getInstance().findById(memberId);
    }

    public void saveMemberAddress(MemberAddress address)
    {
        MemberAddressManager.getInstance().save(address);
    }

    public MemberAddress getMemberAddress(Integer id)
    {
        return MemberAddressManager.getInstance().findById(id);
    }

    public List<MemberAddress> findMemberAddress(Integer memberId)
    {
        return MemberAddressManager.getInstance().findAll(memberId);
    }

    public void authenticationMember(Integer memberId,String name,String identityCardNo,String identityCardImg,
                         String bankName,String bankAccount)
    {
        MemberInfoManager.getInstance().authentication(memberId,name,identityCardNo,identityCardImg,bankName,bankAccount);
    }

    public void verifyAuthentication(Integer memberId)
    {
        MemberManager.getInstance().verifyAuthStatus(memberId);
    }

    public void saveMemberBankInfo(MemberBankInfo bankInfo)
    {
        MemberBankInfoManager.getInstance().save(bankInfo);
    }

    public MemberBankInfo getMemberBankInfo(Integer id)
    {
        return MemberBankInfoManager.getInstance().findById(id);
    }

    public Page<MemberBankInfo> findMemberBankInfo(String cellPhone,String name,Integer status,Integer pageIndex,
                                                   Integer pageSize)
    {
        return MemberBankInfoManager.getInstance().findAll(cellPhone,name,status,pageIndex,pageSize);
    }

    public void createInviteCode(Integer memberId)
    {
        InviteCodeManager.getInstance().create(memberId);
    }

    public void addInviteCodeCount(Integer memberId,Integer count)
    {
        MemberManager.getInstance().addInviteCount(memberId,count);
    }

    public Page<InviteCode> findInviteCode(Integer ownerId, String ownerCellPhone, int pageIndex, int pageSize)
    {
        return InviteCodeManager.getInstance().findAll(ownerId,ownerCellPhone,pageIndex,pageSize);
    }

    public MemberStore applyStore(Integer memberId,String inviteCode)
    {
        return MemberStoreManager.getInstance().apply(memberId,inviteCode);
    }

    public Page<MemberStore> findMemberStore(String cellPhone,Integer status,Integer pageIndex,Integer pageSize)
    {
        return MemberStoreManager.getInstance().findAll(cellPhone,status,pageIndex,pageSize);
    }

    public MemberAuth saveMemberAuth(MemberAuth auth)
    {
        return MemberAuthManager.getInstance().save(auth);
    }

    public MemberAuth getMemberAuth(String platform)
    {
        return  null;
    }

    public void createSystemUser(SystemUser user)
    {
        SystemUserManager.getInstance().create(user);
    }

    public SystemUser loginSystemUser(String userName,String password)
    {
        return SystemUserManager.getInstance().login(userName,password);
    }

    public void lockSystemUser(Integer userId)
    {
        SystemUserManager.getInstance().lock(userId);
    }

    public void updateSystemUserPassword(Integer memberId,String oldPassword,String newPassword,String reNewPassword)
    {
        SystemUserManager.getInstance().updatePassword(memberId,oldPassword,newPassword,reNewPassword);
    }

    public Page<SystemUser> findSystemUser(String userName, Integer pageIndex, Integer pageSize)
    {
        return SystemUserManager.getInstance().findAll(userName,pageIndex,pageSize);
    }
}

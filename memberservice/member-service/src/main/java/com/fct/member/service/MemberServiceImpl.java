package com.fct.member.service;


import com.fct.member.data.entity.*;
import com.fct.member.interfaces.MemberDTO;
import com.fct.member.interfaces.PageResponse;
import com.fct.member.service.business.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Autowired
    private SysUserLoginManager sysUserLoginManager;

    @Autowired
    private MemberFavouriteManager memberFavouriteManager;

    /*注册会员*/
//    @Transactional
    public Member registerMember(String cellPhone, String userName, String password)
    {
        return memberManager.register(cellPhone,userName,password);
    }
    public void logoutMember(String token)
    {
        memberLoginManager.logOut(token);
    }
    /*普通登录和快捷登录*/
    public MemberLogin loginMember(String cellPhone, String password, String ip,Integer expireDay)
    {
        if(StringUtils.isEmpty(password))
        {
            return memberLoginManager.quickLogin(cellPhone,ip,expireDay);
        }
        return memberLoginManager.login(cellPhone,password,ip,expireDay);
    }

    public MemberLogin getMemberLogin(String token)
    {
        return memberLoginManager.findByToken(token);
    }

    public Member getMember(Integer memberId)
    {
        return memberManager.findById(memberId);
    }

    public MemberDTO getMemberDTO(Integer memberId)
    {
        return memberInfoManager.findByMemberId(memberId);
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

    public PageResponse<Member> findMember(String cellPhone,Integer authStatus, String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        return memberManager.findAll(cellPhone,authStatus,beginTime,endTime,pageIndex,pageSize);
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

    public MemberAddress getDefaultAddress(Integer memberId)
    {
        return memberAddressManager.findByDefault(memberId);
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

    public PageResponse<MemberBankInfo> findMemberBankInfo(String cellPhone,String bankName,Integer status,Integer pageIndex,
                                                   Integer pageSize)
    {
        return memberBankInfoManager.findAll(cellPhone,bankName,status,pageIndex,pageSize);
    }

    public void createInviteCode(Integer memberId)
    {
        inviteCodeManager.create(memberId);
    }

    public void addInviteCodeCount(Integer memberId,Integer count)
    {
        memberManager.addInviteCount(memberId,count);
    }

    public PageResponse<InviteCode> findInviteCode(String code,Integer ownerId, String ownerCellPhone,String toCellphone,
                                                   int pageIndex, int pageSize)
    {
        return inviteCodeManager.findAll(code,ownerId,ownerCellPhone,toCellphone,pageIndex,pageSize);
    }

    public MemberStore applyStore(Integer memberId,String inviteCode)
    {
        return memberStoreManager.apply(memberId,inviteCode);
    }

    public void updateStoreStatus(Integer id)
    {
        memberStoreManager.updateStatus(id);
    }

    public PageResponse<MemberStore> findMemberStore(String cellPhone,Integer status,Integer pageIndex,Integer pageSize)
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

    public SysUserLogin loginSystemUser(String userName,String password,String ip,Integer expireHour)
    {
        return sysUserLoginManager.login(userName,password,ip,expireHour);
    }

    public void logoutSysUser(String token)
    {
        sysUserLoginManager.logOut(token);
    }

    public SysUserLogin getSysUserLogin(String token)
    {
        return sysUserLoginManager.findByToken(token);
    }

    public void lockSystemUser(Integer userId)
    {
        systemUserManager.lock(userId);
    }

    public void updateSystemUserPassword(Integer memberId,String oldPassword,String newPassword,String reNewPassword)
    {
        systemUserManager.updatePassword(memberId,oldPassword,newPassword,reNewPassword);
    }

    public PageResponse<SystemUser> findSystemUser(String userName, Integer pageIndex, Integer pageSize)
    {
        return systemUserManager.findAll(userName,pageIndex,pageSize);
    }

    public void saveFavourite(Integer memberId,Integer favType,Integer relatedId)
    {
        memberFavouriteManager.create(memberId,favType,relatedId);
    }

    public void deleteFavourite(Integer memberId,Integer favType,Integer relatedId)
    {
        memberFavouriteManager.delete(memberId,favType,relatedId);
    }

    public int getFavouriteCount(Integer memberId,Integer favType,Integer relatedId)
    {
        return memberFavouriteManager.getCount(memberId,favType,relatedId);
    }

    public PageResponse<MemberFavourite> findFavourite(Integer memberId,Integer favType,
                                                Integer pageIndex, Integer pageSize)
    {
        return memberFavouriteManager.findAll(memberId,favType,pageIndex,pageSize);
    }
}

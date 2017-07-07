package com.fct.member.service;


import com.fct.member.data.entity.*;
import com.fct.member.interfaces.MemberDTO;
import com.fct.member.interfaces.PageResponse;
import com.fct.member.service.business.*;
import org.hibernate.boot.spi.InFlightMetadataCollector;
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
        try {
            return memberManager.register(cellPhone, userName, password);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }
    public void logoutMember(String token)
    {
        try {
            memberLoginManager.logOut(token);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }
    /*普通登录和快捷登录*/
    public MemberLogin loginMember(String cellPhone, String password, String platform,String ip,Integer expireDay)
    {
        try {
            if(StringUtils.isEmpty(password))
            {
                return memberLoginManager.quickLogin(cellPhone,platform,ip,expireDay);
            }
            return memberLoginManager.login(cellPhone,password,platform,ip,expireDay);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public MemberLogin getMemberLogin(String token)
    {
        try {
            return memberLoginManager.findByToken(token);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public Member getMember(Integer memberId)
    {
        try {
            return memberManager.findById(memberId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public MemberDTO getMemberDTO(Integer memberId)
    {
        try {

            return memberInfoManager.findByMemberId(memberId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    /*修改密码*/
    public void updateMemberPassword(Integer memberId,String oldPassword,String newPassword,String reNewPassword)
    {
        try {
            memberManager.updatePassword(memberId,oldPassword,newPassword,reNewPassword);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void forgetPassword(String cellPhone,String password)
    {
        try {
            memberManager.forgetPassword(cellPhone,password);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void lockMember(Integer memberId)
    {
        try {
            memberManager.lock(memberId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<Member> findMember(String cellPhone,Integer authStatus, String beginTime, String endTime, Integer pageIndex, Integer pageSize)
    {
        try {
            return memberManager.findAll(cellPhone, authStatus, beginTime, endTime, pageIndex, pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void updateMemberInfo(MemberInfo info)
    {
        try {
            memberInfoManager.save(info);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public MemberInfo getMemberInfo(Integer memberId)
    {
        try {
            return memberInfoManager.findById(memberId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void saveMemberAddress(MemberAddress address)
    {
        try {
            memberAddressManager.save(address);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public MemberAddress getMemberAddress(Integer id)
    {
        try {
            return memberAddressManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public List<MemberAddress> findMemberAddress(Integer memberId)
    {
        try {
            return memberAddressManager.findAll(memberId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public MemberAddress getDefaultAddress(Integer memberId)
    {
        try {
            return memberAddressManager.findByDefault(memberId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void authenticationMember(Integer memberId,String name,String identityCardNo,String identityCardImg,
                         String bankName,String bankAccount)
    {
        try {
            memberInfoManager.authentication(memberId,name,identityCardNo,identityCardImg,bankName,bankAccount);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void verifyAuthentication(Integer memberId)
    {
        try {
            memberManager.verifyAuthStatus(memberId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void saveMemberBankInfo(MemberBankInfo bankInfo)
    {
        try {
            memberBankInfoManager.save(bankInfo);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public MemberBankInfo getMemberBankInfo(Integer id)
    {
        try {
            return memberBankInfoManager.findById(id);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public PageResponse<MemberBankInfo> findMemberBankInfo(String cellPhone,String bankName,Integer status,Integer pageIndex,
                                                   Integer pageSize)
    {
        try {
            return memberBankInfoManager.findAll(cellPhone,bankName,status,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void createInviteCode(Integer memberId)
    {
        try {
            inviteCodeManager.create(memberId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void addInviteCodeCount(Integer memberId,Integer count)
    {
        try {
            memberManager.addInviteCount(memberId,count);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<InviteCode> findInviteCode(String code,Integer ownerId, String ownerCellPhone,String toCellphone,
                                                   int pageIndex, int pageSize)
    {
        try {
            return inviteCodeManager.findAll(code,ownerId,ownerCellPhone,toCellphone,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public MemberStore applyStore(Integer memberId,String inviteCode)
    {
        try {
            return memberStoreManager.apply(memberId,inviteCode);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void updateStoreStatus(Integer id)
    {
        try {
            memberStoreManager.updateStatus(id);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<MemberStore> findMemberStore(String cellPhone,Integer status,Integer pageIndex,Integer pageSize)
    {
        try {
            return memberStoreManager.findAll(cellPhone,status,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public MemberAuth saveMemberAuth(MemberAuth auth)
    {
        try {
            return memberAuthManager.save(auth);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public MemberAuth getMemberAuth(String platform)
    {
        return  null;
    }

    public void createSystemUser(SystemUser user)
    {
        try {
            systemUserManager.create(user);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public SysUserLogin loginSystemUser(String userName,String password,String ip,Integer expireHour)
    {
        try {
            return sysUserLoginManager.login(userName,password,ip,expireHour);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void logoutSysUser(String token)
    {
        try {
            sysUserLoginManager.logOut(token);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public SysUserLogin getSysUserLogin(String token)
    {

        try {
            return sysUserLoginManager.findByToken(token);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void lockSystemUser(Integer userId)
    {
        try {
            systemUserManager.lock(userId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void updateSystemUserPassword(Integer memberId,String oldPassword,String newPassword,String reNewPassword)
    {
        try {
            systemUserManager.updatePassword(memberId,oldPassword,newPassword,reNewPassword);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public PageResponse<SystemUser> findSystemUser(String userName, Integer pageIndex, Integer pageSize)
    {
        try {
            return systemUserManager.findAll(userName,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }

    public void saveFavourite(Integer memberId,Integer favType,Integer relatedId)
    {
        try {
            memberFavouriteManager.create(memberId,favType,relatedId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public void deleteFavourite(Integer memberId,Integer favType,Integer relatedId)
    {
        try {
            memberFavouriteManager.delete(memberId,favType,relatedId);
        }
        catch (IllegalArgumentException exp)
        {
            throw exp;
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
    }

    public int getFavouriteCount(Integer memberId,Integer favType,Integer relatedId)
    {
        try {
            return memberFavouriteManager.getCount(memberId,favType,relatedId);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return 0;
    }

    public PageResponse<MemberFavourite> findFavourite(Integer memberId,Integer favType,
                                                Integer pageIndex, Integer pageSize)
    {
        try {
            return memberFavouriteManager.findAll(memberId,favType,pageIndex,pageSize);
        }
        catch (Exception exp)
        {
            Constants.logger.error(exp.toString());
        }
        return null;
    }
}

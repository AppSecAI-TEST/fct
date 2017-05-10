package com.fct.member.service.business;

import com.fct.common.utils.DateUtils;
import com.fct.member.data.entity.InviteCode;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.MemberStore;
import com.fct.member.data.repository.MemberStoreRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/7.
 */
@Service
public class MemberStoreManager {

    // 将自身的实例对象设置为一个属性,并加上Static和final修饰符
    private static final MemberStoreManager instance = new MemberStoreManager();

    // 静态方法返回该类的实例
    public static MemberStoreManager getInstance() {
        return instance;
    }

    @Autowired
    private MemberStoreRepository memberStoreRepository;

    public MemberStore findByMemberId(Integer memberId)
    {
        return memberStoreRepository.findByMemberId(memberId);
    }

    @Transient
    public  MemberStore apply(Integer memberId,String inviteCode)
    {
        InviteCode code = InviteCodeManager.getInstance().findByCode(inviteCode);

        if(DateUtils.compareDate(new Date(),code.getExpireTime())>0 || code.getStatus() !=0)
        {
            throw new IllegalArgumentException("邀请码无效。");
        }
        Member member = MemberManager.getInstance().findById(memberId);

        if(memberStoreRepository.findByMemberId(memberId) !=null)
        {
            throw new IllegalArgumentException("已有店铺存在。");
        }

        code.setStatus(1);//已使用
        code.setToMemberId(memberId);
        code.setToCellPhone(member.getCellPhone());
        code.setUseTime(new Date());
        InviteCodeManager.getInstance().save(code);

        MemberStore ms = new MemberStore();
        ms.setMemberId(memberId);
        ms.setCreateTime(new Date());
        ms.setCellPhone(member.getCellPhone());
        ms.setStatus(0);
        memberStoreRepository.save(ms);

        return ms;
    }

    public Page<MemberStore> findAll(String cellPhone, Integer status, Integer pageIndex, Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<MemberStore> spec = new Specification<MemberStore>() {
            @Override
            public Predicate toPredicate(Root<MemberStore> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(cellPhone)) {
                    predicates.add(cb.equal(root.get("cellPhone"), cellPhone));
                }
                if(status>-1)
                {
                    predicates.add(cb.equal(root.get("status"),status));
                }

                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return memberStoreRepository.findAll(spec,pageable);
    }
}

package com.fct.member.service.business;

import com.fct.common.utils.DateUtils;
import com.fct.common.utils.StringHelper;
import com.fct.member.data.entity.InviteCode;
import com.fct.member.data.entity.Member;
import com.fct.member.data.entity.MemberStore;
import com.fct.member.data.repository.InviteCodeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/8.
 */
@Service
public class InviteCodeManager {

    @Autowired
    private InviteCodeRepository inviteCodeRepository;

    @Autowired
    private MemberManager memberManager;

    @Transactional
    public void create(Integer memberId)
    {
        Member member = memberManager.findById(memberId);
        if (member == null || member.getCanInviteCount() == 0)
        {
            throw new IllegalArgumentException("您没有生成邀请码权利！");
        }

        InviteCode code = new InviteCode();
        code.setOwnerId(member.getId());
        code.setOwnerCellPhone(member.getCellPhone());
        code.setCreateTime(new Date());
        code.setExpireTime(DateUtils.addDay(new Date(),7));   //邀请码7天有效期
        code.setCode(StringHelper.getRandomString(8));  //随机8位

        inviteCodeRepository.save(code);

        member.setCanInviteCount(member.getCanInviteCount()-1);
        memberManager.save(member);
    }

    public Page<InviteCode> findAll(Integer ownerId, String ownerCellPhone, int pageIndex, int pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<InviteCode> spec = new Specification<InviteCode>() {
            @Override
            public Predicate toPredicate(Root<InviteCode> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(ownerCellPhone)) {
                    predicates.add(cb.equal(root.get("ownerCellPhone"), ownerCellPhone));
                }
                if(ownerId>0)
                {
                    predicates.add(cb.equal(root.get("ownerId"),ownerId));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return inviteCodeRepository.findAll(spec,pageable);
    }

    public InviteCode findByCode(String code)
    {
        return  inviteCodeRepository.findByCode(code);
    }

    public void save(InviteCode code)
    {
        inviteCodeRepository.saveAndFlush(code);
    }
}

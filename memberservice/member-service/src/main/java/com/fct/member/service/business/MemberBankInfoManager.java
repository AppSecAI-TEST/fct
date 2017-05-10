package com.fct.member.service.business;

import com.fct.member.data.entity.MemberBankInfo;
import com.fct.member.data.repository.MemberBankInfoRepository;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jon on 2017/5/8.
 */
@Service
public class MemberBankInfoManager {

    // 将自身的实例对象设置为一个属性,并加上Static和final修饰符
    private static final MemberBankInfoManager instance = new MemberBankInfoManager();

    // 静态方法返回该类的实例
    public static MemberBankInfoManager getInstance() {
        return instance;
    }

    @Autowired
    private MemberBankInfoRepository memberBankInfoRepository;

    public void save(MemberBankInfo info)
    {
        memberBankInfoRepository.saveAndFlush(info);
    }

    public MemberBankInfo findById(Integer id)
    {
        return memberBankInfoRepository.findOne(id);
    }

    public MemberBankInfo findOne(Integer memberId)
    {
        return memberBankInfoRepository.findOne(memberId);
    }

    public Page<MemberBankInfo> findAll(String cellPhone,String name,Integer status,Integer pageIndex,
                                        Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "Id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<MemberBankInfo> spec = new Specification<MemberBankInfo>() {
            @Override
            public Predicate toPredicate(Root<MemberBankInfo> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(cellPhone)) {
                    predicates.add(cb.equal(root.get("cellPhone"), cellPhone));
                }
                if (!StringUtils.isEmpty(name)) {
                    predicates.add(cb.like(root.get("name"), name));
                }
                if(status>-1)
                {
                    predicates.add(cb.equal(root.get("Status"),status));
                }
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return memberBankInfoRepository.findAll(spec,pageable);
    }
}

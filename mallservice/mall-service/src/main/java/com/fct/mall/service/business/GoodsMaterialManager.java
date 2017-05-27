package com.fct.mall.service.business;

import com.fct.mall.data.entity.GoodsMaterial;
import com.fct.mall.data.repository.GoodsMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jon on 2017/5/22.
 * Im glad we're on this one way street just you and I
   Just you and I
 */
@Service
public class GoodsMaterialManager {

    @Autowired
    private GoodsMaterialRepository goodsMaterialRepository;

    public void save(GoodsMaterial goodsMaterial) {
        goodsMaterial.setUpdateTime(new Date());
        if (goodsMaterial.getId() > 0) {
            goodsMaterialRepository.saveAndFlush(goodsMaterial);
        } else {
            goodsMaterial.setCreateTime(new Date());
            goodsMaterialRepository.save(goodsMaterial);
        }
    }

    public GoodsMaterial findById(Integer id) {
        return goodsMaterialRepository.findOne(id);
    }

    public void updateStatus(Integer id)
    {
        goodsMaterialRepository.updateStatus(id,new Date().toString());
    }

    public Page<GoodsMaterial> findAll(Integer goodsId, String name, Integer status, Integer pageIndex, Integer pageSize)
    {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);

        Specification<GoodsMaterial> spec = new Specification<GoodsMaterial>() {
            @Override
            public Predicate toPredicate(Root<GoodsMaterial> root,
                                         CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(name)) {
                    predicates.add(cb.like(root.get("name"), name));
                }
                if(status>-1)
                {
                    predicates.add(cb.equal(root.get("status"),status));
                }

                if (goodsId>0) {
                    predicates.add(cb.equal(root.get("goodsId"), goodsId));
                }

                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        };

        return goodsMaterialRepository.findAll(spec,pageable);

    }
}

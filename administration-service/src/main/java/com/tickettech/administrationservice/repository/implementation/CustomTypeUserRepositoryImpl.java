package com.tickettech.administrationservice.repository.implementation;

import com.tickettech.administrationservice.dto.input.TypeUserFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultTypeUserDTO;
import com.tickettech.administrationservice.entity.TypeUser;
import com.tickettech.administrationservice.repository.CustomTypeUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomTypeUserRepositoryImpl implements CustomTypeUserRepository {

    @Autowired
    private EntityManager entityManager;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomTypeUserRepositoryImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<ResultTypeUserDTO> findAllWithCriteria(TypeUserFilterDTO filterDTO, Pageable pageable) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<TypeUser> query = cb.createQuery(TypeUser.class);
            Root<TypeUser> typeUser = query.from(TypeUser.class);

            List<Predicate> predicates = new ArrayList<>();

            if (filterDTO.getId() != null && filterDTO.getId() > 0) {
                predicates.add(cb.equal(typeUser.get("id"), filterDTO.getId()));
            }

            if (filterDTO.getIdCompany() != null && filterDTO.getIdCompany() > 0) {
                predicates.add(cb.equal(typeUser.get("idCompany"), filterDTO.getIdCompany()));
            }

            if (filterDTO.getIdSubsidiary() != null && filterDTO.getIdSubsidiary() > 0) {
                predicates.add(cb.equal(typeUser.get("idSubsidiary"), filterDTO.getIdSubsidiary()));
            }

            if (filterDTO.getName() != null && !filterDTO.getName().isEmpty()) {
                predicates.add(cb.like(cb.lower(typeUser.get("name")), "%" + filterDTO.getName().trim().toLowerCase() + "%"));
            }

            if (filterDTO.getIsActive() != null) {
                predicates.add(cb.equal(typeUser.get("isActive"), filterDTO.getIsActive()));
            }

            query.where(predicates.toArray(new Predicate[0]));
            query.orderBy(cb.asc(typeUser.get("name")));

            TypedQuery<TypeUser> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());

            List<TypeUser> resultList = typedQuery.getResultList();

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<TypeUser> typeUserCountRoot = countQuery.from(TypeUser.class);
            countQuery.select(cb.count(typeUserCountRoot)).where(predicates.toArray(new Predicate[0]));

            Long count = entityManager.createQuery(countQuery).getSingleResult();

            List<ResultTypeUserDTO> dtos = resultList.stream()
                    .map(entity -> modelMapper.map(entity, ResultTypeUserDTO.class))
                    .collect(Collectors.toList());

            return new PageImpl<>(dtos, pageable, count);
        } catch (Exception err) {
            return null;
        }
    }
}

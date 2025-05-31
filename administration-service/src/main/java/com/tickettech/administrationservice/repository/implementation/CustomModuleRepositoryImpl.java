package com.tickettech.administrationservice.repository.implementation;

import com.tickettech.administrationservice.dto.input.ModuleFilterDTO;
import com.tickettech.administrationservice.dto.input.TypeUserFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultModuleDTO;
import com.tickettech.administrationservice.dto.output.ResultTypeUserDTO;
import com.tickettech.administrationservice.entity.TypeUser;
import com.tickettech.administrationservice.repository.CustomModuleRepository;
import com.tickettech.administrationservice.repository.CustomTypeUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class CustomModuleRepositoryImpl implements CustomModuleRepository {

    @Autowired
    private EntityManager entityManager;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomModuleRepositoryImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<ResultModuleDTO> findAllWithCriteria(ModuleFilterDTO filterDTO, Pageable pageable) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Module> query = cb.createQuery(Module.class);
            Root<Module> module = query.from(Module.class);

            List<Predicate> predicates = new ArrayList<>();

            if (filterDTO.getId() != null && filterDTO.getId() > 0) {
                predicates.add(cb.equal(module.get("id"), filterDTO.getId()));
            }

            if (filterDTO.getName() != null && !filterDTO.getName().isEmpty()) {
                predicates.add(cb.like(cb.lower(module.get("name")), "%" + filterDTO.getName().trim().toLowerCase() + "%"));
            }

            if (filterDTO.getState() != null) {
                predicates.add(cb.equal(module.get("state"), filterDTO.getState()));
            }

            query.where(predicates.toArray(new Predicate[0]));
            query.orderBy(cb.asc(module.get("name")));

            TypedQuery<Module> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());

            List<Module> resultList = typedQuery.getResultList();

            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Module> moduleCountRoot = countQuery.from(Module.class);
            countQuery.select(cb.count(moduleCountRoot)).where(predicates.toArray(new Predicate[0]));

            Long count = entityManager.createQuery(countQuery).getSingleResult();

            List<ResultModuleDTO> dtos = resultList.stream()
                    .map(entity -> modelMapper.map(entity, ResultModuleDTO.class))
                    .collect(Collectors.toList());

            return new PageImpl<>(dtos, pageable, count);
        } catch (Exception err) {
            return null;
        }
    }
}

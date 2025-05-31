package com.tickettech.administrationservice.repository.implementation;

import com.tickettech.administrationservice.dto.input.CompanyFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultCompanyDTO;
import com.tickettech.administrationservice.entity.Company;
import com.tickettech.administrationservice.repository.CustomCompanyRepository;
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
public class CustomCompanyRepositoryImpl implements CustomCompanyRepository {

    private final EntityManager entityManager;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomCompanyRepositoryImpl(EntityManager entityManager, ModelMapper modelMapper) {
        this.entityManager = entityManager;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<ResultCompanyDTO> findAllWithCriteria(CompanyFilterDTO filterDTO, Pageable pageable) {
        if (filterDTO == null) {
            throw new IllegalArgumentException("filterDTO no puede ser nulo");
        }

        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            // Consulta principal (datos)
            CriteriaQuery<Company> dataQuery = cb.createQuery(Company.class);
            Root<Company> companyRoot = dataQuery.from(Company.class);

            List<Predicate> predicates = buildPredicates(cb, companyRoot, filterDTO);

            dataQuery.select(companyRoot)
                    .where(cb.and(predicates.toArray(new Predicate[0])))
                    .orderBy(cb.asc(companyRoot.get("name"))); // Ordena por nombre

            TypedQuery<Company> typedQuery = entityManager.createQuery(dataQuery);
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());

            List<Company> resultList = typedQuery.getResultList();

            // Consulta de conteo (total de elementos)
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Company> countRoot = countQuery.from(Company.class);
            countQuery.select(cb.count(countRoot))
                    .where(cb.and(buildPredicates(cb, countRoot, filterDTO).toArray(new Predicate[0])));

            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

            // Mapear resultados a DTOs
            List<ResultCompanyDTO> dtos = resultList.stream()
                    .map(entity -> modelMapper.map(entity, ResultCompanyDTO.class))
                    .collect(Collectors.toList());

            return new PageImpl<>(dtos, pageable, totalElements);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error en findAllWithCriteria", ex);
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Company> companyRoot, CompanyFilterDTO filterDTO) {
        List<Predicate> predicates = new ArrayList<>();

        if (filterDTO.getId() != null && filterDTO.getId() > 0) {
            predicates.add(cb.equal(companyRoot.get("id"), filterDTO.getId()));
        }
        if (filterDTO.getImage() != null && !filterDTO.getImage().isEmpty()) {
            predicates.add(cb.like(cb.lower(companyRoot.get("image")), "%" + filterDTO.getImage().toLowerCase() + "%"));
        }
        if (filterDTO.getName() != null && !filterDTO.getName().isEmpty()) {
            predicates.add(cb.like(cb.lower(companyRoot.get("name")), "%" + filterDTO.getName().toLowerCase() + "%"));
        }
        if (filterDTO.getNit() != null && !filterDTO.getNit().isEmpty()) {
            predicates.add(cb.equal(companyRoot.get("nit"), filterDTO.getNit()));
        }
        if (filterDTO.getState() != null) {
            predicates.add(cb.equal(companyRoot.get("state"), filterDTO.getState()));
        }

        return predicates;
    }
}

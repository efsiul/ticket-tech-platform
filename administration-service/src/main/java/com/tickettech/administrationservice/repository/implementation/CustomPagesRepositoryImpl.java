package com.tickettech.administrationservice.repository.implementation;

import com.tickettech.administrationservice.dto.input.PagesFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultPagesDTO;
import com.tickettech.administrationservice.entity.Pages;
import com.tickettech.administrationservice.repository.CustomPagesRepository;
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
public class CustomPagesRepositoryImpl implements CustomPagesRepository {

    @Autowired
    private EntityManager entityManager;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomPagesRepositoryImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<ResultPagesDTO> findAllWithCriteria(PagesFilterDTO filterDTO, Pageable pageable) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            // Consulta principal (datos)
            CriteriaQuery<Pages> dataQuery = cb.createQuery(Pages.class);
            Root<Pages> pagesRoot = dataQuery.from(Pages.class);

            List<Predicate> predicates = buildPredicates(cb, pagesRoot, filterDTO);

            dataQuery.select(pagesRoot)
                    .where(cb.and(predicates.toArray(new Predicate[0])))
                    .orderBy(cb.asc(pagesRoot.get("name"))); // Ordena por nombre

            TypedQuery<Pages> typedQuery = entityManager.createQuery(dataQuery);
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());

            List<Pages> resultList = typedQuery.getResultList();

            // Consulta de conteo (total de elementos)
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<Pages> countRoot = countQuery.from(Pages.class); // Nueva raíz para evitar conflictos
            countQuery.select(cb.count(countRoot))
                    .where(cb.and(buildPredicates(cb, countRoot, filterDTO).toArray(new Predicate[0])));

            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

            // Mapear resultados a DTOs
            List<ResultPagesDTO> dtos = resultList.stream()
                    .map(entity -> modelMapper.map(entity, ResultPagesDTO.class))
                    .collect(Collectors.toList());

            return new PageImpl<>(dtos, pageable, totalElements);

        } catch (Exception ex) {
            System.err.println("Error executing findAllWithCriteria: " + ex.getMessage());
            throw ex;
        }
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Pages> pagesRoot, PagesFilterDTO filterDTO) {
        List<Predicate> predicates = new ArrayList<>();

        if (filterDTO.getId() != null && filterDTO.getId()>0) {
            predicates.add(cb.equal(pagesRoot.get("id"), filterDTO.getId()));
        }
        if (filterDTO.getNpage() != null && !filterDTO.getNpage().isEmpty()) {
            predicates.add(cb.like(cb.lower(pagesRoot.get("npage")), "%" + filterDTO.getNpage().toLowerCase() + "%"));
        }
        if (filterDTO.getIcon() != null && !filterDTO.getIcon().isEmpty()) {
            predicates.add(cb.like(cb.lower(pagesRoot.get("icon")), "%" + filterDTO.getIcon().toLowerCase() + "%"));
        }
        if (filterDTO.getName() != null && !filterDTO.getName().isEmpty()) {
            predicates.add(cb.like(cb.lower(pagesRoot.get("name")), "%" + filterDTO.getName().toLowerCase() + "%"));
        }
        if (filterDTO.getIdUser() != null && filterDTO.getIdUser() > 0) {
            predicates.add(cb.equal(pagesRoot.get("idUser"), filterDTO.getIdUser()));
        }
        if (filterDTO.getIdModule() != null && filterDTO.getIdModule() > 0) {
            predicates.add(cb.equal(pagesRoot.get("idModule"), filterDTO.getIdModule()));
        }
        if (filterDTO.getIsActive() != null) {
            predicates.add(cb.equal(pagesRoot.get("isActive"), filterDTO.getIsActive()));
        }

        return predicates;
    }
}

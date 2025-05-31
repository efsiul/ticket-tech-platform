package com.tickettech.administrationservice.repository.implementation;

import com.tickettech.administrationservice.dto.input.PageTypeUserFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultPageTypeUserDTO;
import com.tickettech.administrationservice.entity.PageTypeUser;
import com.tickettech.administrationservice.entity.Pages;
import com.tickettech.administrationservice.repository.CustomPageTypeUserRepository;
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
public class CustomPageTypeUserRepositoryImpl implements CustomPageTypeUserRepository {

    @Autowired
    private EntityManager entityManager;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomPageTypeUserRepositoryImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<ResultPageTypeUserDTO> findAllWithCriteria(PageTypeUserFilterDTO filterDTO, Pageable pageable) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();

            // 1) Construir la consulta de datos
            CriteriaQuery<PageTypeUser> dataQuery = cb.createQuery(PageTypeUser.class);
            Root<PageTypeUser> root = dataQuery.from(PageTypeUser.class);

            // 2) Armar predicados
            List<Predicate> predicates = buildPredicates(cb, root, filterDTO);

            // 3) Selección + WHERE + ORDER BY
            dataQuery.select(root)
                    .where(cb.and(predicates.toArray(new Predicate[0])))
                    // Ordenar por la PK del PageTypeUser (campo "id") para evitar ambigüedad
                    .orderBy(cb.asc(root.get("id")));

            TypedQuery<PageTypeUser> typedQuery = entityManager.createQuery(dataQuery);
            typedQuery.setFirstResult((int) pageable.getOffset());
            typedQuery.setMaxResults(pageable.getPageSize());
            List<PageTypeUser> resultList = typedQuery.getResultList();

            // 4) Conteo total
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<PageTypeUser> countRoot = countQuery.from(PageTypeUser.class);
            countQuery.select(cb.count(countRoot))
                    .where(cb.and(buildPredicates(cb, countRoot, filterDTO).toArray(new Predicate[0])));
            Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

            // 5) Mapear a DTO
            List<ResultPageTypeUserDTO> dtos = resultList.stream()
                    .map(entity -> modelMapper.map(entity, ResultPageTypeUserDTO.class))
                    .toList();

            return new PageImpl<>(dtos, pageable, totalElements);

        } catch (Exception ex) {
            System.err.println("Error executing findAllWithCriteria: " + ex.getMessage());
            throw ex;
        }
    }


    @Override
    public Page<ResultPageTypeUserDTO> findAllPagesByIdTypeUser(Long idTypeUser, Boolean isActive, Pageable pageable) {
        PageTypeUserFilterDTO filterDTO = new PageTypeUserFilterDTO();
        filterDTO.setIdTypeUser(idTypeUser);
        filterDTO.setIsActive(isActive);
        return findAllWithCriteria(filterDTO, pageable);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<PageTypeUser> root, PageTypeUserFilterDTO filterDTO) {
        List<Predicate> predicates = new ArrayList<>();

        // 1) Filtrar por ID (PK de PageTypeUser)
        if (filterDTO.getId() != null && filterDTO.getId() > 0) {
            predicates.add(cb.equal(root.get("id"), filterDTO.getId()));
        }

        // 2) Filtrar por “idPage”: unirse a la colección “pages” y comparar el campo “id” de Pages
        if (filterDTO.getIdPage() != null && filterDTO.getIdPage() > 0) {
            Join<PageTypeUser, Pages> pagesJoin = root.join("pages", JoinType.LEFT);
            predicates.add(cb.equal(pagesJoin.get("id"), filterDTO.getIdPage()));
        }

        // 3) Filtrar por “idTypeUser”
        if (filterDTO.getIdTypeUser() != null && filterDTO.getIdTypeUser() > 0) {
            predicates.add(cb.equal(root.get("typeUser").get("id"), filterDTO.getIdTypeUser()));
        }

        // 4) Filtrar por “idUser”
        if (filterDTO.getIdUser() != null && filterDTO.getIdUser() > 0) {
            predicates.add(cb.equal(root.get("user").get("id"), filterDTO.getIdUser()));
        }

        // 5) Filtrar flags booleanos
        if (filterDTO.getIsCreate() != null) {
            predicates.add(cb.equal(root.get("isCreate"), filterDTO.getIsCreate()));
        }
        if (filterDTO.getIsUpdate() != null) {
            predicates.add(cb.equal(root.get("isUpdate"), filterDTO.getIsUpdate()));
        }
        if (filterDTO.getIsActive() != null) {
            predicates.add(cb.equal(root.get("isActive"), filterDTO.getIsActive()));
        }

        return predicates;
    }

}

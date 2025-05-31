package com.tickettech.administrationservice.repository.implementation;

import com.tickettech.administrationservice.dto.input.UserFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultTypeUserDTO;
import com.tickettech.administrationservice.dto.output.ResultUserDTO;
import com.tickettech.administrationservice.entity.User;
import com.tickettech.administrationservice.repository.CustomUserRepository;
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
public class CustomUserRepositoryImpl implements CustomUserRepository {

    @Autowired
    private EntityManager entityManager;
    private final ModelMapper modelMapper;

    @Autowired
    public CustomUserRepositoryImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

@Override
public Page<ResultUserDTO> findAllWithCriteria(UserFilterDTO filter, Pageable pageable) {
    try {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Consulta principal (datos)
        CriteriaQuery<User> dataQuery = cb.createQuery(User.class);
        Root<User> userRoot = dataQuery.from(User.class);

        List<Predicate> predicates = buildPredicates(cb, userRoot, filter);

        dataQuery.select(userRoot)
                .where(cb.and(predicates.toArray(new Predicate[0])))
                .orderBy(cb.asc(userRoot.get("name"))); // Ordena por nombre

        TypedQuery<User> typedQuery = entityManager.createQuery(dataQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<User> resultList = typedQuery.getResultList();

        // Limpia la contraseña
        resultList.forEach(user -> user.setPassword(""));

        // Consulta de conteo (total de elementos)
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countRoot = countQuery.from(User.class); // NUEVA raíz para evitar conflictos
        countQuery.select(cb.count(countRoot))
                .where(cb.and(buildPredicates(cb, countRoot, filter).toArray(new Predicate[0])));

        Long totalElements = entityManager.createQuery(countQuery).getSingleResult();

        // Mapear resultados a DTOs
        List<ResultUserDTO> dtos = resultList.stream()
                .map(user -> modelMapper.map(user, ResultUserDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, totalElements);

    } catch (Exception ex) {
        System.err.println("Error executing findAllWithCriteria: " + ex.getMessage());
        throw ex;
    }
}

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<User> userRoot, UserFilterDTO filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getId() != null && filter.getId() > 0) {
            predicates.add(cb.equal(userRoot.get("id"), filter.getId()));
        }
        if (filter.getIdCompany() != null && filter.getIdCompany() > 0) {
            predicates.add(cb.equal(userRoot.get("idCompany"), filter.getIdCompany()));
        }
        if (filter.getIdSubsidiary() != null && filter.getIdSubsidiary() > 0) {
            predicates.add(cb.equal(userRoot.get("idSubsidiary"), filter.getIdSubsidiary()));
        }
        if (filter.getName() != null && !filter.getName().isEmpty()) {
            predicates.add(cb.like(cb.lower(userRoot.get("name")), "%" + filter.getName().trim().toLowerCase() + "%"));
        }
        if (filter.getLastName() != null && !filter.getLastName().isEmpty()) {
            predicates.add(cb.like(cb.lower(userRoot.get("lastName")), "%" + filter.getLastName().trim().toLowerCase() + "%"));
        }
        if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
            predicates.add(cb.like(cb.lower(userRoot.get("email")), "%" + filter.getEmail().trim().toLowerCase() + "%"));
        }
        if (filter.getCellphone() != null && !filter.getCellphone().isEmpty()) {
            predicates.add(cb.like(cb.lower(userRoot.get("cellphone")), "%" + filter.getCellphone().trim().toLowerCase() + "%"));
        }
        if (filter.getIdTypeUser() != null && filter.getIdTypeUser() > 0) {
            predicates.add(cb.equal(userRoot.get("idTypeUser"), filter.getIdTypeUser()));
        }
        if (filter.getIsActive() != null) {
            predicates.add(cb.equal(userRoot.get("isActive"), filter.getIsActive()));
        }
        if (filter.getIsAdmin() != null) {
            predicates.add(cb.equal(userRoot.get("isAdmin"), filter.getIsAdmin()));
        }

        return predicates;
    }

}

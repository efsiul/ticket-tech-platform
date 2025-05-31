package com.tickettech.administrationservice.repository;

import com.tickettech.administrationservice.entity.Pages;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface PagesRepository extends JpaRepository<Pages, Long> {
    Optional<Pages> findByName(String name);

}

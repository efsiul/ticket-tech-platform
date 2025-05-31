package com.tickettech.administrationservice.repository;

import com.tickettech.administrationservice.entity.PageTypeUser;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface PageTypeUserRepository extends JpaRepository<PageTypeUser, Long> {
    Collection<PageTypeUser> findAllByPages_IdAndTypeUser_Id(Long pageId, Long typeUserId);
}

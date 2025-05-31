package com.tickettech.administrationservice.repository;

import com.tickettech.administrationservice.dto.input.UserFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultUserDTO;
import com.tickettech.administrationservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface CustomUserRepository {
    Page<ResultUserDTO> findAllWithCriteria(UserFilterDTO filter, Pageable pageable);
}
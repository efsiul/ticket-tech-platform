package com.tickettech.administrationservice.repository;

import com.tickettech.administrationservice.dto.input.PageTypeUserFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultPageTypeUserDTO;
import com.tickettech.administrationservice.entity.PageTypeUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPageTypeUserRepository {
    Page<ResultPageTypeUserDTO> findAllWithCriteria(PageTypeUserFilterDTO filterDTO, Pageable pageable);

    Page<ResultPageTypeUserDTO> findAllPagesByIdTypeUser(Long idTypeUser,
                                                Boolean isActive,
                                                Pageable pageable);
}
package com.tickettech.administrationservice.repository;

import com.tickettech.administrationservice.dto.input.TypeUserFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultTypeUserDTO;
import com.tickettech.administrationservice.entity.TypeUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomTypeUserRepository {
    Page<ResultTypeUserDTO> findAllWithCriteria(TypeUserFilterDTO filterDTO, Pageable pageable);
}
package com.tickettech.administrationservice.repository;

import com.tickettech.administrationservice.dto.input.ModuleFilterDTO;
import com.tickettech.administrationservice.dto.input.UserFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultModuleDTO;
import com.tickettech.administrationservice.dto.output.ResultUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomModuleRepository {
    Page<ResultModuleDTO> findAllWithCriteria(ModuleFilterDTO filter, Pageable pageable);
}
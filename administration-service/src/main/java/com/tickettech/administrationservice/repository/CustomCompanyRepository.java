package com.tickettech.administrationservice.repository;

import com.tickettech.administrationservice.dto.input.CompanyFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultCompanyDTO;
import com.tickettech.administrationservice.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomCompanyRepository {
    Page<ResultCompanyDTO> findAllWithCriteria(CompanyFilterDTO filterDTO, Pageable pageable);
}
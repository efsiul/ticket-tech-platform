package com.tickettech.administrationservice.repository;

import com.tickettech.administrationservice.dto.input.PagesFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultPagesDTO;
import com.tickettech.administrationservice.entity.Pages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPagesRepository {
    Page<ResultPagesDTO> findAllWithCriteria(PagesFilterDTO filterDTO, Pageable pageable);
}
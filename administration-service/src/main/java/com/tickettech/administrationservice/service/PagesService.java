package com.tickettech.administrationservice.service;

import com.tickettech.administrationservice.dto.input.PagesDTO;
import com.tickettech.administrationservice.dto.input.PagesFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultDTO;

public interface PagesService {

    public abstract ResultDTO saveAndUpdate(PagesDTO pagesDTO, String language)  throws Exception;
    public abstract ResultDTO getById(long id, String language) throws Exception;
    public abstract ResultDTO getAllItems(PagesFilterDTO pagesFilterDTO, String language) throws Exception;
}
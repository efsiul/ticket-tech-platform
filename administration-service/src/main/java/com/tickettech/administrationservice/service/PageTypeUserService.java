package com.tickettech.administrationservice.service;

import com.tickettech.administrationservice.dto.input.PageTypeUserByTypeFilterDTO;
import com.tickettech.administrationservice.dto.input.PageTypeUserFilterDTO;
import com.tickettech.administrationservice.dto.input.PageTypeUserSaveDTO;
import com.tickettech.administrationservice.dto.output.ResultDTO;

public interface PageTypeUserService {

    public abstract ResultDTO saveAndUpdate(PageTypeUserSaveDTO pageTypeUserSaveDTO, String language)  throws Exception;
    public abstract ResultDTO getById(long id, String language) throws Exception;
    public abstract ResultDTO getByIdTypeUser(PageTypeUserByTypeFilterDTO filterDTO, String language) throws Exception;
    public abstract ResultDTO getAllItems(PageTypeUserFilterDTO pageTypeUserFilterDTO, String language) throws Exception;
}
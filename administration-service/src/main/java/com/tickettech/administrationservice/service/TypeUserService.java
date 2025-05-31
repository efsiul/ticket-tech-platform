package com.tickettech.administrationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tickettech.administrationservice.dto.input.TypeUserDTO;
import com.tickettech.administrationservice.dto.input.TypeUserFilterDTO;
import com.tickettech.administrationservice.dto.output.ResultDTO;

import java.net.URISyntaxException;

public interface TypeUserService {

    public abstract ResultDTO saveAndUpdate(TypeUserDTO typeUserDTO, String language) throws Exception;
    public abstract ResultDTO getById(long id, String language) throws Exception;
    public abstract ResultDTO getAllItems(TypeUserFilterDTO filterDTO, String language) throws URISyntaxException, JsonProcessingException;
}
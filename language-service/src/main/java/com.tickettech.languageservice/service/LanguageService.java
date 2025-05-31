package com.tickettech.languageservice.service;

import com.tickettech.languageservice.dto.input.LanguageDTO;
import com.tickettech.languageservice.dto.output.ResultDTO;

public interface LanguageService {

    public abstract ResultDTO saveAndUpdate(LanguageDTO languageDTO);

    public abstract ResultDTO getById(String id);

    public abstract ResultDTO getAllItems(int size, int page);
}
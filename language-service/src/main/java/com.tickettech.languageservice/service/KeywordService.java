package com.tickettech.languageservice.service;

import com.tickettech.languageservice.dto.input.KeywordDTO;
import com.tickettech.languageservice.dto.output.ResultDTO;

public interface KeywordService {

    public abstract ResultDTO saveAndUpdate(KeywordDTO keywordDTO);

    public abstract ResultDTO getById(String id);

    public abstract ResultDTO getAllItems(int size, int page);
}


package com.tickettech.languageservice.service;

import com.tickettech.languageservice.dto.input.MessageLabelsDTO;
import com.tickettech.languageservice.dto.output.ResultDTO;

public interface MessageLabelsService {

    public abstract ResultDTO saveAndUpdate(MessageLabelsDTO messageLabelsDTO);

    public abstract ResultDTO getById(String id);

    public abstract ResultDTO getAllItems(int size, int page);
}
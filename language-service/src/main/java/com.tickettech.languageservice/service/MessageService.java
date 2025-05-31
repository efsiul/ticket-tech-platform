
package com.tickettech.languageservice.service;

import com.tickettech.languageservice.dto.output.ResultDTO;

import java.util.List;

public interface MessageService {
    public ResultDTO getMessageByKey(String key, String idLanguage);

    public ResultDTO getMessageListByKey(List<String> keys, String idLanguage);

}
package com.tickettech.languageservice.service.implementation;

import com.tickettech.languageservice.dto.input.MessageLabelsDTO;
import com.tickettech.languageservice.dto.output.ResultDTO;
import com.tickettech.languageservice.entity.MessageLabels;
import com.tickettech.languageservice.repository.MessageLabelsRepository;
import com.tickettech.languageservice.service.MessageLabelsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageLabelsServiceImpl implements MessageLabelsService {

    private final MessageLabelsRepository repository;

    public MessageLabelsServiceImpl(MessageLabelsRepository repository) {
        this.repository = repository;
    }

    public ResultDTO saveAndUpdate(MessageLabelsDTO messageLabelsDTO) {
        MessageLabels messageLabels = new MessageLabels();
        if (messageLabelsDTO.id() != null && !"".equals(messageLabelsDTO.id().trim())) {
            Optional<MessageLabels> optionalLanguage = repository.findById(messageLabelsDTO.id());
            if (optionalLanguage.isPresent()) {
                messageLabels = optionalLanguage.get();
                messageLabels.setLabel(messageLabelsDTO.label());
                messageLabels.setIdkey(messageLabelsDTO.idkey());
                messageLabels.setIdlanguage(messageLabelsDTO.idlanguage());
                messageLabels.setLastUpdate(LocalDateTime.now());
            } else {
                messageLabels.setId(messageLabelsDTO.id());
                messageLabels.setLabel(messageLabelsDTO.label());
                messageLabels.setIdkey(messageLabelsDTO.idkey());
                messageLabels.setIdlanguage(messageLabelsDTO.idlanguage());
                messageLabels.setCreation(LocalDateTime.now());
                messageLabels.setLastUpdate(LocalDateTime.now());
            }
        } else {
            messageLabels.setId(UUID.randomUUID().toString());
            messageLabels.setLabel(messageLabelsDTO.label());
            messageLabels.setIdkey(messageLabelsDTO.idkey());
            messageLabels.setIdlanguage(messageLabelsDTO.idlanguage());
            messageLabels.setCreation(LocalDateTime.now());
            messageLabels.setLastUpdate(LocalDateTime.now());
        }
        messageLabels = repository.save(messageLabels);
        return new ResultDTO(messageLabels);
    }

    @Override
    public ResultDTO getById(String id) {
        return new ResultDTO(repository.findAllById(id));
    }

    @Override
    public ResultDTO getAllItems(int size, int page) {
        return new ResultDTO(repository.findAll(PageRequest.of(page, size)));
    }
}

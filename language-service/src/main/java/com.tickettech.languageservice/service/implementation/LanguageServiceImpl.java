package com.tickettech.languageservice.service.implementation;

import com.tickettech.languageservice.dto.input.LanguageDTO;
import com.tickettech.languageservice.dto.output.ResultDTO;
import com.tickettech.languageservice.entity.Language;
import com.tickettech.languageservice.repository.LanguageRepository;
import com.tickettech.languageservice.service.LanguageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository repository;

    public LanguageServiceImpl(LanguageRepository repository) {
        this.repository = repository;
    }

    @Override
    public ResultDTO saveAndUpdate(LanguageDTO languageDTO) {
        Language language = new Language();
        if (languageDTO.id() != null && !"".equals(languageDTO.id().trim())) {
            Optional<Language> optionalLanguage = repository.findById(languageDTO.id());
            if (optionalLanguage.isPresent()) {
                language = optionalLanguage.get();
                language.setLanguage(languageDTO.language());
                language.setLastUpdate(LocalDateTime.now());
            } else {
                language.setId(languageDTO.id());
                language.setLanguage(languageDTO.language());
                language.setCreation(LocalDateTime.now());
                language.setLastUpdate(LocalDateTime.now());
            }
        } else {
            language.setId(UUID.randomUUID().toString());
            language.setLanguage(languageDTO.language());
            language.setCreation(LocalDateTime.now());
            language.setLastUpdate(LocalDateTime.now());
        }
        language = repository.save(language);
        return new ResultDTO(language);
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

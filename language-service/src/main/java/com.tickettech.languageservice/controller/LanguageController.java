package com.tickettech.languageservice.controller;

import com.tickettech.languageservice.dto.input.LanguageDTO;
import com.tickettech.languageservice.dto.output.ResultDTO;
import com.tickettech.languageservice.service.LanguageService;
import com.tickettech.languageservice.service.implementation.LanguageServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/language")
public class LanguageController {

    private final LanguageService launguageService;

    public LanguageController(LanguageServiceImpl launguageService) {
        this.launguageService = launguageService;
    }

    @GetMapping(value = "/allLanguages/{page}/{size}")
    public ResultDTO getLanguages(@PathVariable(name = "size") int size,
            @PathVariable(name = "page") int page) {
        return launguageService.getAllItems(size, page);
    }

    @PostMapping(value = "/addLanguage")
    public ResultDTO saveLanguage(@RequestBody LanguageDTO languageDTO) {
        return launguageService.saveAndUpdate(languageDTO);
    }

    @GetMapping(value = "/getByIdLanguage/{id}", produces = "application/json")
    public ResultDTO getOneLanguageById(@PathVariable String id) {
        return launguageService.getById(id);
    }
}
package com.tickettech.languageservice.dto.input;

import com.tickettech.languageservice.entity.Keyword;

import java.util.List;

public record KeywordPageDTO(
                int page,
                int size,
                int totalPage,
                List<Keyword> keywordDTOList) {
}

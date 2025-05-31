package com.tickettech.languageservice.dto.input;

import java.util.List;

public record MessageLabelsPageDTO(
                int page,
                int size,
                int totalPage,
                List<MessageLabelsDTO> messageLabelsDTOList) {
}

package com.tickettech.administrationservice.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for managing the relationship between pages and user types.")
public class PageTypeUserFilterDTO {
        private Long id;

        @NotNull(message = "idPage cannot be null")
        private Long idPage;

        @NotNull(message = "idTypeUser cannot be null")
        private Long idTypeUser;

        @NotNull(message = "idUser cannot be null")
        private Long idUser;

        private Boolean isCreate;
        private Boolean isUpdate;
        private Boolean isActive;

        @NotNull(message = "Size cannot be null.")
        private int size;

        @NotNull(message = "Page cannot be null.")
        private int page;
}
package com.tickettech.administrationservice.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data required to save or update a type user.")
public class TypeUserFilterDTO {

        @Schema(description = "Unique identifier of the type user.", example = "1")
        private Long id;

        @Schema(description = "Unique identifier of the company.", example = "200")
        private Long idCompany;

        @Schema(description = "Unique identifier of the subsidiary.", example = "10")
        private Long idSubsidiary;

        @Schema(description = "Name of the type user.", example = "Admin")
        private String name;

        @Schema(description = "Indicates whether the type user is active.", example = "true")
        private Boolean isActive;

        @Schema(description = "Number of records per page.", example = "10", required = true)
        @NotNull(message = "Size cannot be null.")
        private int size;

        @Schema(description = "Page number to retrieve.", example = "1", required = true)
        @NotNull(message = "Page cannot be null.")
        private int page;

}

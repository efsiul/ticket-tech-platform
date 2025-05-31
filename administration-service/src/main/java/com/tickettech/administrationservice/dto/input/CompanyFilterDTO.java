package com.tickettech.administrationservice.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data required to save or update a company.")
public class CompanyFilterDTO {

        @Schema(description = "Unique identifier of the company.", example = "1")
        private Long id;

        @Schema(description = "Name of the company.", example = "Rhiscomtest")
        private String name;

        @Schema(description = "Tax identification number of the company.", example = "1234561")
        private String nit;

        @Schema(description = "image for a Company.", example = "image1.jpg")
        private String image;

        @Schema(description = "State of the company.", example = "true")
        private Boolean state;

        @Schema(description = "Number of records per page.", example = "10", required = true)
        @NotNull(message = "Size cannot be null.")
        private int size;

        @Schema(description = "Page number to retrieve.", example = "1", required = true)
        @NotNull(message = "Page cannot be null.")
        private int page;

}


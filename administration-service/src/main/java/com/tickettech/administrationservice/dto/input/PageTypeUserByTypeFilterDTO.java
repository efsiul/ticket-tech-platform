package com.tickettech.administrationservice.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filter DTO for querying the relationship between pages and user types.")
public record PageTypeUserByTypeFilterDTO(
        @Schema(description = "Unique identifier of the user.", example = "user1") Long idUser,
        @Schema(description = "Number of items per page.", example = "10") int size,
        @Schema(description = "Page number for pagination.", example = "0") int page
) {}

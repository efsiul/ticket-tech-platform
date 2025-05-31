package com.tickettech.administrationservice.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for managing the relationship between pages and user types.")
public class PageTypeUserDTO {
    @Schema(description = "Unique identifier.", example = "1")
    private Long id;

    @Schema(description = "Unique identifier of the page.", example = "page1")
    @NotNull(message = "idPage cannot be null")
    private Long idPage;

    @Schema(description = "Unique identifier of the user type.", example = "type1")
    @NotNull(message = "idTypeUser cannot be null")
    private Long idTypeUser;

    @Schema(description = "Unique identifier of the user.", example = "user1")
    @NotNull(message = "idUser cannot be null")
    private Long idUser;

    @Schema(description = "Flag indicating if the page can be created.", example = "true")
    private Boolean isCreate;

    @Schema(description = "Flag indicating if the page can be updated.", example = "true")
    private Boolean isUpdate;

    @Schema(description = "Flag indicating if the page is active.", example = "true")
    private Boolean isActive;
}

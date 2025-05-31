package com.tickettech.administrationservice.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO representing the permissions for a page in the system.")
public class PagesListDTO {

    @Schema(description = "Unique identifier of the page.", example = "page1")
    private Long idPage;

    @Schema(description = "Flag indicating if create permission is granted.", example = "true")
    private Boolean isCreate;

    @Schema(description = "Flag indicating if update permission is granted.", example = "true")
    private Boolean isUpdate;

    @Schema(description = "Flag indicating if the page is active.", example = "true")
    private Boolean isActive;

}

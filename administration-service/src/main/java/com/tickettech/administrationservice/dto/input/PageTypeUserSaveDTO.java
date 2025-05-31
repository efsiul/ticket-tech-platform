package com.tickettech.administrationservice.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "DTO for saving page-type-user relationships.")
public class PageTypeUserSaveDTO {

    @Schema(description = "Unique identifier.", example = "1")
    private Long id;

    @Schema(description = "Unique identifier of the user type.", example = "type1")
    private Long idTypeUser;

    @Schema(description = "Unique identifier of the user.", example = "user1")
    private Long idUser;

    @Schema(description = "List of pages with their respective permissions.")
    private List<PagesListDTO> pages;
}

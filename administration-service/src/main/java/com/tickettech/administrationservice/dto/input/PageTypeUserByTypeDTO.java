package com.tickettech.administrationservice.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO for managing the relationship between pages and user types.")
public class PageTypeUserByTypeDTO {

    @Schema(description = "Unique identifier of the page.", example = "page1")
    private Long idPage;

    @Schema(description = "Icon associated with the page.", example = "dashboard_icon")
    private String icon;

    @Schema(description = "Navigation path of the page.", example = "dashboard")
    private String npage;

    @Schema(description = "Navigation path of the page.", example = "dashboard")
    private String namePage;

    @Schema(description = "Unique identifier of the user type.", example = "type1")
    private Long idTypeUser;

    @Schema(description = "Flag indicating if the page can be created.", example = "true")
    private Boolean isCreate;

    @Schema(description = "Flag indicating if the page can be updated.", example = "true")
    private Boolean isUpdate;

    @Schema(description = "Flag indicating if the page is active.", example = "true")
    private Boolean isActive;

}

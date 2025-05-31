package com.tickettech.administrationservice.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data required to save or update a page.")
public class PagesDTO {

    @Schema(description = "Unique identifier of the page.", example = "1")
    private Long id;

    @Schema(description = "Number of the page.", example = "Page1")
    private String npage;

    @Schema(description = "Icon of the page.", example = "icon.png")
    private String icon;

    @Schema(description = "Name of the page.", example = "Dashboard")
    private String name;

    @Schema(description = "ID of the user making the request.", example = "1")
    private Long idUser;

    @Schema(description = "ID of the module to which the page belongs.", example = "module1")
    private Long idModule;

    @Schema(description = "State of the page.", example = "true")
    private Boolean isActive;

}

package com.tickettech.administrationservice.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data required to save or update a module.")
public class ModuleDTO {

    @Schema(description = "Unique identifier of the type user.", example = "1")
    private Long id;

    @Schema(description = "Name of the type user.", example = "Admin")
    private String name;

    @Schema(description = "Indicates whether module is active.", example = "true")
    private Boolean state;

    @Schema(description = "ID of the user making the request.", example = "100")
    private Long idUser;

}

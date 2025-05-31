package com.tickettech.administrationservice.dto.output;

import com.tickettech.administrationservice.entity.Pages;
import com.tickettech.administrationservice.entity.TypeUser;
import com.tickettech.administrationservice.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Schema(description = "DTO for managing the relationship between pages and user types.")
public class ResultPageTypeUserDTO {
    @Schema(description = "Unique identifier.", example = "1")
    private Long id;

    @Schema(description = "Unique identifier of the page.", example = "page1")
    @NotNull(message = "idPage cannot be null")
    private Set<Pages> pages;

    @Schema(description = "Unique identifier of the user type.", example = "type1")
    @NotNull(message = "idTypeUser cannot be null")
    private TypeUser typeUser;

    @Schema(description = "Unique identifier of the user.", example = "user1")
    @NotNull(message = "idUser cannot be null")
    private User user;

    @Schema(description = "Flag indicating if the page can be created.", example = "true")
    private Boolean isCreate;

    @Schema(description = "Flag indicating if the page can be updated.", example = "true")
    private Boolean isUpdate;

    @Schema(description = "Flag indicating if the page is active.", example = "true")
    private Boolean isActive;
}

package com.tickettech.administrationservice.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data required to save or update a user.")
public class UserFilterDTO {

    @Schema(description = "Unique identifier of the user.", example = "1")
    private Long id;

    @Schema(description = "Unique identifier of the company.", example = "3")
    private Long idCompany;

    @Schema(description = "Unique identifier of the subsidiary.", example = "2")
    private Long idSubsidiary;

    @Schema(description = "First name of the user.", example = "John")
    private String name;

    @Schema(description = "Last name of the user.", example = "Doe")
    private String lastName;

    @Schema(description = "Email address of the user.", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Cellphone number of the user.", example = "+571234567890")
    private String cellphone;

    @Schema(description = "ID of the type of user.", example = "4")
    private Long idTypeUser;

    @JsonProperty("isActive")
    @Schema(description = "Indicates if the user is active.", example = "true")
    private Boolean isActive;

    @JsonProperty("isAdmin")
    @Schema(description = "Indicates if the user is an admin.", example = "false")
    private Boolean isAdmin;

    @Schema(description = "Number of records per page.", example = "10", required = true)
    @NotNull(message = "Size cannot be null.")
    private int size;

    @Schema(description = "Page number to retrieve.", example = "1", required = true)
    @NotNull(message = "Page cannot be null.")
    private int page;
}

package com.tickettech.administrationservice.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tickettech.administrationservice.entity.Company;
import com.tickettech.administrationservice.entity.TypeUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data required to save or update a user.")
public class ResultUserDTO {

    @Schema(description = "Unique identifier of the user.", example = "1")
    private Long id;

    @Schema(description = "Unique identifier of the subsidiary.", example = "2")
    private Long idSubsidiary;

    @Schema(description = "Name of the subsidiary.", example = "Subsidiary Name")
    private String subsidiaryName;

    @Schema(description = "Unique identifier of the company.", example = "3")
    private Company company;

    @Schema(description = "First name of the user.", example = "John")
    private String name;

    @Schema(description = "Last name of the user.", example = "Doe")
    private String lastName;

    @Schema(description = "Email address of the user.", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Cellphone number of the user.", example = "+571234567890")
    private String cellphone;

    @Schema(description = "Password of the user.", example = "encryptedPassword")
    private String password;

    @Schema(description = "ID of the type of user.", example = "4")
    private TypeUser typeUser;

    @JsonProperty("isActive")
    @Schema(description = "Indicates if the user is active.", example = "true")
    private Boolean isActive;

    @JsonProperty("isAdmin")
    @Schema(description = "Indicates if the user is an admin.", example = "false")
    private Boolean isAdmin;

}

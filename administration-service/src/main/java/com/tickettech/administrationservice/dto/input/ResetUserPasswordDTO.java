package com.tickettech.administrationservice.dto.input;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data required to reset a user's password.")
public class ResetUserPasswordDTO {

    @Schema(description = "Unique identifier of the user.", example = "1")
    private Long id;

    @Schema(description = "ID of the subsidiary.", example = "2")
    private Long idSubsidiary;

    @Schema(description = "New encrypted password.", example = "newEncryptedPassword")
    private String password;

    @Schema(description = "Old encrypted password.", example = "oldEncryptedPassword")
    private String oldPassword;

}

package com.tickettech.administrationservice.dto.output;

import com.tickettech.administrationservice.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Data required to save or update a company.")
public class ResultCompanyDTO {

    @Schema(description = "Unique identifier of the company.", example = "1")
    private Long id;

    @Schema(description = "Name of the company.", example = "Rhiscomtest")
    private String name;

    @Schema(description = "Tax identification number of the company.", example = "1234561")
    private String nit;

    @Schema(description = "Address of the company.", example = "cl 1#1-1")
    private String address;

    @Schema(description = "Contact person for the company.", example = "anderson")
    private String contactPerson;

    @Schema(description = "Phone number of the company.", example = "+57123456")
    private String phone;

    @Schema(description = "Email address of the company.", example = "abc@abc")
    private String mail;

    @Schema(description = "image for a Company.", example = "image1.jpg")
    private String image;

    @Schema(description = "State of the company.", example = "true")
    private Boolean state;

    @Schema(description = "ID of the user making the request.", example = "100")
    private User user;

}


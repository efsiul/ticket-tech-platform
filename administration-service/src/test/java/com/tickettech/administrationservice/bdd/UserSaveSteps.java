package com.tickettech.administrationservice.bdd;

import com.tickettech.administrationservice.dto.input.UserDTO;
import com.tickettech.administrationservice.dto.output.ResultDTO;
import com.tickettech.administrationservice.service.UserService;
import io.cucumber.java.en.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class UserSaveSteps {

    private UserDTO userDTO;
    private ResultDTO response;
    private String language;
    private UserService userService;

    @Given("se desea guardar un usuario con email {string} y nombre {string}")
    public void crearNuevoUsuario(String email, String name) throws Exception {
        userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setName(name);
        language = "es";

        ResultDTO mockResponse = new ResultDTO();
        mockResponse.setCode(200);
        mockResponse.setMessage("Usuario guardado exitosamente");

        userService = Mockito.mock(UserService.class);
        Mockito.when(userService.saveAndUpdate(any(UserDTO.class), Mockito.eq(language)))
               .thenReturn(mockResponse);
    }

    @Given("se intenta guardar un usuario ya existente con email {string}")
    public void usuarioExistente(String email) throws Exception {
        userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setName("Nombre X");
        language = "es";

        ResultDTO mockResponse = new ResultDTO();
        mockResponse.setCode(102);
        mockResponse.setMessage("El usuario ya existe");

        userService = Mockito.mock(UserService.class);
        Mockito.when(userService.saveAndUpdate(any(UserDTO.class), Mockito.eq(language)))
               .thenReturn(mockResponse);
    }

    @When("el cliente envía la solicitud de guardado con lenguaje {string}")
    public void enviarSolicitud(String lang) throws Exception {
        language = lang;
        response = userService.saveAndUpdate(userDTO, language);
    }

    @Then("el sistema debe responder con código {int} y mensaje {string}")
    public void validarRespuesta(int expectedCode, String expectedMessage) {
        assertNotNull(response);
        assertEquals(expectedCode, response.getCode());
        assertEquals(expectedMessage, response.getMessage());
    }
}

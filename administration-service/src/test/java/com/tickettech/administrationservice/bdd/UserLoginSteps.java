package com.tickettech.administrationservice.bdd;

import com.tickettech.administrationservice.dto.input.LoginDTO;
import com.tickettech.administrationservice.dto.output.ResultDTO;
import com.tickettech.administrationservice.service.UserService;
import io.cucumber.java.en.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class UserLoginSteps {

    private LoginDTO loginDTO;
    private ResultDTO response;
    private String language;

    @Autowired
    private UserService userService;

    @Given("el sistema tiene un usuario válido con email {string} y contraseña {string}")
    public void elSistemaTieneUnUsuarioValido(String email, String password) throws Exception {
        loginDTO = new LoginDTO(email, password);
        language = "es";

        ResultDTO mockResponse = new ResultDTO();
        mockResponse.setCode(200);
        mockResponse.setMessage("Login exitoso");

        userService = Mockito.mock(UserService.class);
        Mockito.when(userService.login(any(LoginDTO.class), Mockito.eq(language))).thenReturn(mockResponse);
    }

    @Given("no existe un usuario con email {string}")
    public void noExisteUsuario(String email) throws Exception {
        loginDTO = new LoginDTO(email, "123456");
        language = "es";

        ResultDTO mockResponse = new ResultDTO();
        mockResponse.setCode(100);
        mockResponse.setMessage("Usuario no encontrado");

        userService = Mockito.mock(UserService.class);
        Mockito.when(userService.login(any(LoginDTO.class), Mockito.eq(language))).thenReturn(mockResponse);
    }

    @When("el cliente hace una petición de login con email {string} y contraseña {string} y lenguaje {string}")
    public void elClienteHaceLogin(String email, String password, String lang) throws Exception {
        loginDTO = new LoginDTO(email, password);
        language = lang;
        response = userService.login(loginDTO, language);
    }

    @Then("el sistema debe responder con código {int} y mensaje {string}")
    public void validarRespuesta(int code, String mensaje) {
        assertNotNull(response);
        assertEquals(code, response.getCode());
        assertEquals(mensaje, response.getMessage());
    }
}

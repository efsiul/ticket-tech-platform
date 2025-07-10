Feature: Login de usuario

  Scenario: Usuario inicia sesión correctamente
    Given el sistema tiene un usuario válido con email "test@correo.com" y contraseña "123456"
    When el cliente hace una petición de login con email "test@correo.com" y contraseña "123456" y lenguaje "es"
    Then el sistema debe responder con código 200 y mensaje "Login exitoso"

  Scenario: Usuario no existe
    Given no existe un usuario con email "inexistente@correo.com"
    When el cliente hace una petición de login con email "inexistente@correo.com" y contraseña "123456" y lenguaje "es"
    Then el sistema debe responder con código 100 y mensaje "Usuario no encontrado"

Feature: Crear o actualizar usuario

  Scenario: Guardar un nuevo usuario exitosamente
    Given se desea guardar un usuario con email "nuevo@correo.com" y nombre "Juan Pérez"
    When el cliente envía la solicitud de guardado con lenguaje "es"
    Then el sistema debe responder con código 200 y mensaje "Usuario guardado exitosamente"

  Scenario: El usuario ya existe y no se puede guardar
    Given se intenta guardar un usuario ya existente con email "existente@correo.com"
    When el cliente envía la solicitud de guardado con lenguaje "es"
    Then el sistema debe responder con código 102 y mensaje "El usuario ya existe"

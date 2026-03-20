@autenticacion
Feature: Autenticación de usuario en la plataforma

  @flujo-positivo
  Scenario: Inicio de sesión exitoso con credenciales válidas
    Given el usuario se encuentra en la pantalla de inicio de sesión
    When el usuario ingresa las credenciales "VALIDAS"
    And envía el formulario de inicio de sesión
    Then el usuario accede exitosamente al dashboard

  @flujo-negativo
  Scenario: Inicio de sesión fallido con credenciales inválidas
    Given el usuario se encuentra en la pantalla de inicio de sesión
    When el usuario ingresa las credenciales "INVALIDAS"
    And envía el formulario de inicio de sesión
    Then el sistema muestra un mensaje de error de autenticación

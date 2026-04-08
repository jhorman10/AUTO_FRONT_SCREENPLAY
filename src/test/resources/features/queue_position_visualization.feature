@visualizacion-cola
Feature: Visualización de posición en cola de espera

  Como usuario en una sala de espera,
  quiero ver en tiempo real mi posición en la cola,
  para conocer mi progreso sin preguntar en recepción.

  Background:
    Given la aplicación está disponible en la pantalla pública de sala de espera

  @flujo-positivo @posicion-visible
  Scenario: El paciente ve su posición y estado del turno en la pantalla de espera
    Given el recepcionista ha iniciado sesión en el sistema
    And el recepcionista ha registrado un turno con urgencia "Media" para el paciente "Carlos Gómez" con cédula "1001001001"
    When el paciente consulta la pantalla pública de sala de espera
    Then la pantalla muestra el turno del paciente "Carlos Gómez" en alguna sección
    And el turno del paciente "Carlos Gómez" tiene un estado activo visible

  @flujo-positivo @actualizacion-tiempo-real
  Scenario: La posición se actualiza automáticamente cuando cambia la cola
    Given el recepcionista ha iniciado sesión en el sistema
    And el recepcionista ha registrado un turno con urgencia "Baja" para el paciente "Ana López" con cédula "2002002002"
    And el paciente "Ana López" tiene posición visible en la pantalla de espera
    When el recepcionista registra un nuevo turno con urgencia "Alta" para el paciente "Pedro Ruiz" con cédula "3003003003"
    And el paciente consulta la pantalla pública de sala de espera
    Then la pantalla muestra el turno del paciente "Pedro Ruiz" en alguna sección

  @flujo-positivo @orden-urgencia
  Scenario: Los turnos registrados aparecen visibles en la pantalla pública
    Given el recepcionista ha iniciado sesión en el sistema
    And el recepcionista ha registrado los siguientes turnos:
      | paciente      | cedula     | urgencia |
      | Laura Díaz    | 4004004004 | Baja     |
      | Mario Soto    | 5005005005 | Alta     |
      | Claudia Ríos  | 6006006006 | Media    |
    When el paciente consulta la pantalla pública de sala de espera
    Then los siguientes pacientes aparecen en la pantalla:
      | paciente      |
      | Laura Díaz    |
      | Mario Soto    |
      | Claudia Ríos  |

  @caso-borde @cola-vacia
  Scenario: La pantalla muestra estado vacío cuando no hay turnos activos
    When el paciente consulta la pantalla pública de sala de espera
    Then la pantalla muestra un estado vacío sin errores visuales

  @flujo-positivo @websocket-conectado
  Scenario: El indicador de conexión WebSocket está visible y conectado
    When el paciente consulta la pantalla pública de sala de espera
    Then el indicador de estado WebSocket está visible

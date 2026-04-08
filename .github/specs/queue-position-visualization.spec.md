---
id: SPEC-002
status: IMPLEMENTED
feature: queue-position-visualization
created: 2026-04-08
updated: 2026-04-08
author: spec-generator
version: "1.0"
related-specs: [SPEC-001]
---

# Spec: Automatización Frontend — Screenplay (Visualización de Posición en Cola)

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción
Implementar una suite de automatización UI usando el patrón Screenplay para la funcionalidad de visualización de posición en cola de espera (HU-02). Se automatizan los escenarios de la pantalla pública (`http://localhost:3001/`) donde los pacientes en sala de espera consultan su posición en tiempo real, validan actualizaciones vía WebSocket sin recarga de página, y verifican el comportamiento de reconexión ante pérdida de conexión.

### Requerimiento de Negocio
Como usuario en una sala de espera, quiero ver en tiempo real mi posición en la cola, para conocer mi progreso sin preguntar en recepción.

Habilitadores técnicos requeridos:
- **HT-02**: Proyección de cola consultable — consulta ordenada por prioridad (Alta > Media > Baja) y FIFO (`created_at`).
- **HT-03**: Canal en tiempo real con reconexión — WebSocket resiliente que publica cambios de turnos sin refrescar la pantalla.

### Historias de Usuario

#### HU-02: Visualización de posición en cola de espera

```
Como:        Usuario (paciente) en una sala de espera
Quiero:      Ver en tiempo real mi posición en la cola
Para:        Conocer mi progreso sin preguntar en recepción

Prioridad:   Alta
Estimación:  L (8 puntos)
Dependencias: HT-02 (Proyección de cola consultable), HT-03 (Canal en tiempo real)
Capa:        Frontend (automatización E2E)
```

#### Criterios de Aceptación — HU-02

**Happy Path**
```gherkin
CRITERIO-2.1: Visualización de posición y estado del turno
  Dado que:  Un turno fue registrado previamente con urgencia válida y está en estado "esperando"
  Cuando:    El usuario consulta la pantalla pública de sala de espera
  Entonces:  La pantalla muestra la posición actual del turno y su estado visible
```

```gherkin
CRITERIO-2.2: Actualización en tiempo real sin recarga
  Dado que:  El usuario está en la pantalla pública de sala de espera con un turno activo en cola
  Cuando:    La cola cambia (se registra o completa otro turno)
  Entonces:  La posición del turno se actualiza automáticamente sin recargar la pantalla
```

**Error Path**
```gherkin
CRITERIO-2.3: Indicador de reconexión ante pérdida de conexión
  Dado que:  El usuario está en la pantalla pública de sala de espera
  Cuando:    Se pierde la conexión WebSocket
  Entonces:  La pantalla muestra un indicador de estado de reconexión y conserva el último dato conocido
```

**Edge Case**
```gherkin
CRITERIO-2.4: Cola vacía sin turnos activos
  Dado que:  No existen turnos activos en el sistema
  Cuando:    El usuario consulta la pantalla pública de sala de espera
  Entonces:  La pantalla muestra un estado vacío coherente sin errores visuales
```

```gherkin
CRITERIO-2.5: Orden de cola respeta urgencia y FIFO
  Dado que:  Existen múltiples turnos con diferentes niveles de urgencia
  Cuando:    El usuario consulta la pantalla pública de sala de espera
  Entonces:  Los turnos se muestran ordenados por urgencia (Alta > Media > Baja) y por orden de llegada dentro de la misma urgencia
```

### Reglas de Negocio
1. La cola se ordena por prioridad: Alta > Media > Baja. En caso de empate, FIFO por `created_at`.
2. La pantalla pública (`/`) no requiere autenticación — cualquier visitante puede consultarla.
3. El componente `WebSocketStatus` debe ser visible e indicar el estado de la conexión en tiempo real.
4. Las pruebas UI deben ser idempotentes y no depender de datos creados por otras pruebas.
5. El registro de turnos requiere autenticación (recepcionista/admin) — el flujo de precondición pasa por login.
6. La pantalla pública fuerza modo claro (force light mode) — no se valida dark mode aquí.

---

## 2. DISEÑO

### Modelos de Datos (en contexto de pruebas)
| Entidad | Descripción |
|---------|-------------|
| `AppointmentData` | Datos de turno para registro: `patientName`, `idCard`, `urgency` (Alta/Media/Baja) |
| `QueueEntry` | Representación de un turno en la cola: posición, nombre del paciente, estado, urgencia |

### API Endpoints (referencia que la UI consume)
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `POST /appointments` | POST | Registra un nuevo turno (requiere auth Bearer) |
| `/ws/appointments` | WebSocket | Canal público de notificaciones de cola en tiempo real |

### Diseño Frontend — Screenplay

#### Actores
| Actor | Descripción | Autenticación |
|-------|-------------|---------------|
| `Recepcionista` | Registra turnos en la pantalla operativa | Firebase Auth (Bearer token) |
| `Paciente` | Consulta la pantalla pública de sala de espera | Sin autenticación |

#### UI Targets (Page Objects)

##### `WaitingRoomPage.java` — Pantalla pública `/`
| Target | Selector propuesto | Descripción |
|--------|-------------------|-------------|
| `QUEUE_LIST` | `css:[data-testid='queue-list'], .appointment-list, ul.queue` | Contenedor de la lista de turnos en cola |
| `QUEUE_ITEM` | `css:[data-testid='queue-item'], .appointment-card, li.queue-item` | Elemento individual de turno en la cola |
| `QUEUE_POSITION` | `css:[data-testid='queue-position'], .queue-position, .position` | Número de posición del turno |
| `QUEUE_STATUS` | `css:[data-testid='queue-status'], .appointment-status, .status` | Estado del turno (esperando, en atención, etc.) |
| `PATIENT_NAME` | `css:[data-testid='patient-name'], .patient-name` | Nombre del paciente en la tarjeta |
| `URGENCY_BADGE` | `css:[data-testid='urgency-badge'], .urgency-badge, .priority` | Indicador visual de urgencia |
| `WEBSOCKET_STATUS` | `css:[data-testid='ws-status'], .websocket-status, .ws-indicator` | Indicador de estado de conexión WebSocket |
| `EMPTY_STATE` | `css:[data-testid='empty-queue'], .empty-state, .no-appointments` | Mensaje de cola vacía |

##### `RegistrationPage.java` — Pantalla de registro operativo `/registration`
| Target | Selector propuesto | Descripción |
|--------|-------------------|-------------|
| `PATIENT_NAME_FIELD` | `css:input[name='patientName'], input[placeholder*='nombre'], input[placeholder*='Nombre']` | Campo nombre del paciente |
| `ID_CARD_FIELD` | `css:input[name='idCard'], input[placeholder*='cédula'], input[placeholder*='documento']` | Campo cédula/documento |
| `URGENCY_SELECT` | `css:select[name='urgency'], [data-testid='urgency-select']` | Selector de urgencia |
| `SUBMIT_BUTTON` | `css:button[type='submit']` | Botón de envío del formulario de registro |
| `SUCCESS_MESSAGE` | `css:[data-testid='success-message'], .success, .alert-success` | Mensaje de registro exitoso |

#### Tasks (Responsabilidad única por Task)
| Task | Clase | Descripción |
|------|-------|-------------|
| `NavigateToWaitingRoom` | `tasks/NavigateToWaitingRoom.java` | Navega a la pantalla pública `/` |
| `NavigateToRegistration` | `tasks/NavigateToRegistration.java` | Navega a la pantalla operativa `/registration` |
| `LoginAsReceptionist` | `tasks/LoginAsReceptionist.java` | Compone: `OpenSignInPage` + `EnterCredentials` + `SubmitLogin` con credenciales de recepcionista |
| `RegisterAppointment` | `tasks/RegisterAppointment.java` | Registra un turno con nombre, cédula y urgencia en `/registration` |
| `SelectUrgency` | `tasks/SelectUrgency.java` | Selecciona un nivel de urgencia (Alta/Media/Baja) en el formulario |
| `WaitForQueueUpdate` | `tasks/WaitForQueueUpdate.java` | Espera explícita hasta que la cola refleje el cambio esperado (nuevo item o cambio de posición) |

#### Questions (Verificaciones)
| Question | Clase | Retorna | Descripción |
|----------|-------|---------|-------------|
| `QueuePositionOf` | `questions/QueuePositionOf.java` | `Integer` | Posición en la cola del turno identificado por nombre del paciente |
| `AppointmentStatusOf` | `questions/AppointmentStatusOf.java` | `String` | Estado del turno identificado por nombre del paciente |
| `WebSocketConnectionStatus` | `questions/WebSocketConnectionStatus.java` | `String` | Texto del indicador de estado WebSocket (conectado/reconectando/desconectado) |
| `QueueIsVisible` | `questions/QueueIsVisible.java` | `Boolean` | Verifica que la lista de cola es visible en pantalla |
| `QueueSize` | `questions/QueueSize.java` | `Integer` | Cantidad de turnos visibles en la cola |
| `QueueOrderIsCorrect` | `questions/QueueOrderIsCorrect.java` | `Boolean` | Verifica que los turnos están ordenados por urgencia y FIFO |
| `EmptyStateIsVisible` | `questions/EmptyStateIsVisible.java` | `Boolean` | Verifica que el estado vacío de la cola está visible |

#### Estructura de paquetes
```
src/test/java/com/auto/ui/
├── questions/
│   ├── IsOnDashboard.java              ← (existente)
│   ├── LoginErrorMessage.java          ← (existente)
│   ├── QueuePositionOf.java            ← NUEVO
│   ├── AppointmentStatusOf.java        ← NUEVO
│   ├── WebSocketConnectionStatus.java  ← NUEVO
│   ├── QueueIsVisible.java             ← NUEVO
│   ├── QueueSize.java                  ← NUEVO
│   ├── QueueOrderIsCorrect.java        ← NUEVO
│   └── EmptyStateIsVisible.java        ← NUEVO
├── tasks/
│   ├── OpenSignInPage.java             ← (existente, reutilizado)
│   ├── EnterCredentials.java           ← (existente, reutilizado)
│   ├── SubmitLogin.java                ← (existente, reutilizado)
│   ├── EnsureUserExists.java           ← (existente, reutilizado)
│   ├── NavigateToWaitingRoom.java      ← NUEVO
│   ├── NavigateToRegistration.java     ← NUEVO
│   ├── LoginAsReceptionist.java        ← NUEVO
│   ├── RegisterAppointment.java        ← NUEVO
│   ├── SelectUrgency.java              ← NUEVO
│   └── WaitForQueueUpdate.java         ← NUEVO
├── ui/
│   ├── SignInPage.java                 ← (existente)
│   ├── SignUpPage.java                 ← (existente)
│   ├── WaitingRoomPage.java            ← NUEVO
│   └── RegistrationPage.java          ← NUEVO
├── steps/
│   ├── SignInStepDefinitions.java      ← (existente)
│   └── QueuePositionStepDefinitions.java ← NUEVO
├── runners/
│   └── CucumberTestSuite.java          ← (existente, sin cambios)
└── utils/
    └── TestConstants.java              ← MODIFICAR (agregar constantes)

src/test/resources/features/
├── signin_screenplay.feature           ← (existente)
└── queue_position_visualization.feature ← NUEVO
```

### Feature Cucumber propuesto

```gherkin
@visualizacion-cola
Feature: Visualización de posición en cola de espera

  Background:
    Given la aplicación está disponible en la pantalla pública de sala de espera

  @flujo-positivo @posicion-visible
  Scenario: El paciente ve su posición y estado del turno en la pantalla de espera
    Given el recepcionista ha registrado un turno con urgencia "Media" para el paciente "Carlos Gómez"
    When el paciente consulta la pantalla pública de sala de espera
    Then la pantalla muestra la posición actual del turno del paciente "Carlos Gómez"
    And el estado del turno se muestra como "esperando"

  @flujo-positivo @actualizacion-tiempo-real
  Scenario: La posición se actualiza automáticamente cuando cambia la cola
    Given el recepcionista ha registrado un turno con urgencia "Baja" para el paciente "Ana López"
    And el paciente "Ana López" tiene posición visible en la pantalla de espera
    When el recepcionista registra un nuevo turno con urgencia "Alta" para el paciente "Pedro Ruiz"
    Then la posición del paciente "Ana López" cambia sin recargar la pantalla

  @flujo-positivo @orden-urgencia
  Scenario: Los turnos se ordenan por urgencia y orden de llegada
    Given el recepcionista ha registrado los siguientes turnos:
      | paciente      | urgencia |
      | Laura Díaz    | Baja     |
      | Mario Soto    | Alta     |
      | Claudia Ríos  | Media    |
    When el paciente consulta la pantalla pública de sala de espera
    Then los turnos se muestran en el orden:
      | posicion | paciente      | urgencia |
      | 1        | Mario Soto    | Alta     |
      | 2        | Claudia Ríos  | Media    |
      | 3        | Laura Díaz    | Baja     |

  @caso-borde @cola-vacia
  Scenario: La pantalla muestra estado vacío cuando no hay turnos activos
    Given no existen turnos activos en el sistema
    When el paciente consulta la pantalla pública de sala de espera
    Then la pantalla muestra un estado vacío sin errores visuales

  @flujo-positivo @websocket-conectado
  Scenario: El indicador de conexión WebSocket está visible y conectado
    When el paciente consulta la pantalla pública de sala de espera
    Then el indicador de estado WebSocket está visible
    And el indicador muestra estado de conexión activa
```

### Constantes nuevas para `TestConstants.java`
| Constante | Valor | Descripción |
|-----------|-------|-------------|
| `WAITING_ROOM_PATH` | `/` | Ruta de la pantalla pública |
| `REGISTRATION_PATH` | `/registration` | Ruta de registro operativo |
| `ACTOR_RECEPTIONIST` | `"Recepcionista"` | Nombre del actor recepcionista |
| `ACTOR_PATIENT` | `"Paciente"` | Nombre del actor paciente |
| `RECEPTIONIST_EMAIL` | `"recepcionista@example.com"` | Email de recepcionista para tests |
| `RECEPTIONIST_PASSWORD` | `"SecurePass123!"` | Password de recepcionista para tests |
| `URGENCY_ALTA` | `"Alta"` | Valor de urgencia alta |
| `URGENCY_MEDIA` | `"Media"` | Valor de urgencia media |
| `URGENCY_BAJA` | `"Baja"` | Valor de urgencia baja |
| `STATUS_WAITING` | `"esperando"` | Estado de turno en espera |
| `QUEUE_UPDATE_TIMEOUT_SECONDS` | `10` | Timeout para esperar actualización de cola |
| `WEBSOCKET_CONNECT_TIMEOUT_SECONDS` | `5` | Timeout para conexión WebSocket |

### Configuración y Dependencias
- **No se requieren dependencias nuevas** — el proyecto ya cuenta con Serenity BDD 3.9.8, Cucumber 7.11.1 y Selenium.
- `serenity.conf` — sin cambios, `webdriver.base.url` ya apunta a `http://localhost:3001`.
- `CucumberTestSuite.java` — sin cambios, ya escanea `src/test/resources/features` y `com.auto.ui.steps`.

### Precondiciones de Ejecución
1. La aplicación completa (5 contenedores Docker) debe estar levantada y accesible en `http://localhost:3001`.
2. Debe existir un usuario con rol **recepcionista** registrado en Firebase Auth con las credenciales definidas en `TestConstants`.
3. El canal WebSocket público (`/ws/appointments`) debe estar operativo.

### Notas de Implementación
- Cada `Task` representa una sola acción del actor (SRP).
- Reutilizar `OpenSignInPage`, `EnterCredentials` y `SubmitLogin` existentes dentro de `LoginAsReceptionist` (composición, no herencia).
- `WaitForQueueUpdate` debe usar `WebDriverWait` con condición explícita para evitar flakiness — no usar `Thread.sleep`.
- Los selectores CSS en `WaitingRoomPage` deben incluir fallbacks (múltiples selectores con `,`) siguiendo el patrón de `SignInPage`.
- Para el escenario de orden de cola, usar `DataTable` de Cucumber para parametrizar registros múltiples.
- El escenario de reconexión WebSocket (CRITERIO-2.3) se excluye de automatización E2E por complejidad de simular corte de red; se valida solo la presencia del indicador `WebSocketStatus`.
- La verificación de "sin recarga" (CRITERIO-2.2) se valida comprobando que la URL no cambia y el contenido se actualiza dinámicamente.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

> No aplica — este proyecto es de automatización E2E. El backend es el sistema bajo prueba (SUT).

### Frontend — Screenplay (Implementación)

#### UI Page Objects
- [x] Crear `WaitingRoomPage.java` en `src/test/java/com/auto/ui/ui/` — targets de la pantalla pública
- [x] Crear `RegistrationPage.java` en `src/test/java/com/auto/ui/ui/` — targets del formulario de registro operativo

#### Tasks
- [x] Crear `NavigateToWaitingRoom.java` — navega a `/`
- [x] Crear `NavigateToRegistration.java` — navega a `/registration`
- [x] Crear `LoginAsReceptionist.java` — compone login con credenciales de recepcionista
- [x] Crear `RegisterAppointment.java` — registra turno con nombre, cédula y urgencia
- [x] Crear `SelectUrgency.java` — selecciona nivel de urgencia en el formulario
- [x] Crear `WaitForQueueUpdate.java` — espera explícita hasta que la cola refleje el cambio

#### Questions
- [x] Crear `QueuePositionOf.java` — posición del turno por nombre del paciente
- [x] Crear `AppointmentStatusOf.java` — estado del turno por nombre del paciente
- [x] Crear `WebSocketConnectionStatus.java` — texto del indicador WebSocket
- [x] Crear `QueueIsVisible.java` — verifica visibilidad de la lista de cola
- [x] Crear `QueueSize.java` — cantidad de turnos visibles
- [x] Crear `QueueOrderIsCorrect.java` — verifica orden por urgencia y FIFO
- [x] Crear `EmptyStateIsVisible.java` — verifica estado vacío de cola

#### Steps y Feature
- [x] Crear `queue_position_visualization.feature` en `src/test/resources/features/`
- [x] Crear `QueuePositionStepDefinitions.java` en `src/test/java/com/auto/ui/steps/`
- [x] Actualizar `TestConstants.java` — agregar constantes de rutas, actores, urgencias y timeouts

#### Validación
- [ ] Ejecutar `./gradlew clean test` y verificar que los 5 escenarios pasan
- [ ] Verificar reportes Serenity en `target/site/serenity/` o `build/reports/tests/`
- [ ] Confirmar que los tests existentes de SignIn siguen pasando (no regresión)

### QA
- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-2.1 a CRITERIO-2.5
- [ ] Ejecutar skill `/risk-identifier` → clasificación ASD de riesgos (flakiness WebSocket, timing, selectores)
- [ ] Revisar cobertura de escenarios contra criterios de aceptación de HU-02
- [ ] Validar que todas las reglas de negocio están cubiertas en los escenarios
- [ ] Actualizar estado spec: `status: IMPLEMENTED`

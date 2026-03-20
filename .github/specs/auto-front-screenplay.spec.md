---
id: SPEC-001
status: DRAFT
feature: AUTO_FRONT_SCREENPLAY
created: 2026-03-19
updated: 2026-03-19
author: spec-generator
version: "1.0"
related-specs: []
---

# Spec: Automatización Frontend — Screenplay (Sign-in)

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.

---

## 1. REQUERIMIENTOS

### Descripción
Implementar una suite de automatización UI usando el patrón Screenplay para la funcionalidad
de autenticación (Sign-in) de la aplicación web en `http://localhost:3001/signin`.
La spec define dos escenarios independientes (al menos 1 positivo y 1 negativo) y la
arquitectura necesaria para mantener pruebas legibles y escalables con Serenity BDD.

### Requerimiento de Negocio
Construir automatizaciones Screenplay en Java/Gradle/Serenity que validen el flujo de
inicio de sesión en la aplicación propia, aportando reportes claros y tareas con
responsabilidad única.

### Historias de Usuario

#### HU-01: Iniciar sesión con credenciales válidas

```
Como:        Usuario no autenticado
Quiero:      Ingresar mis credenciales válidas en la pantalla de signin
Para:        Acceder a mi dashboard personal

Prioridad:   Alta
Estimación:  M
Dependencias: Ninguna
Capa:        Frontend
```

#### Criterios de Aceptación — HU-01

**Happy Path**
```gherkin
CRITERIO-1.1: Inicio de sesión exitoso
  Dado que:  El usuario está en la página de signin (`http://localhost:3001/signin`) con la app levantada
  Cuando:    El usuario ingresa `email` y `password` válidos y envía el formulario
  Entonces:  El usuario es redirigido al dashboard y se muestra un elemento identificable del dashboard
```

**Error Path** *(no aplica para HU-01)*


#### HU-02: Intento de inicio de sesión con credenciales inválidas

```
Como:        Usuario no autenticado
Quiero:      Intentar iniciar sesión con credenciales inválidas
Para:        Ver un mensaje de error y permanecer en la página de signin

Prioridad:   Alta
Estimación:  S
Dependencias: Ninguna
Capa:        Frontend
```

#### Criterios de Aceptación — HU-02

**Error Path**
```gherkin
CRITERIO-2.1: Manejo de credenciales inválidas
  Dado que:  El usuario está en la página de signin
  Cuando:    El usuario ingresa `email` o `password` inválidos y envía el formulario
  Entonces:  Se muestra un mensaje de error visible con el texto esperado y el estatus de la página permanece en signin
```

**Edge Case**
```gherkin
CRITERIO-2.2: Campos vacíos
  Dado que:  El usuario está en la página de signin
  Cuando:    El usuario deja el campo `email` o `password` vacío y envía
  Entonces:  Se muestran validaciones de campo (mensaje inline) y no se envía petición al backend
```

### Reglas de Negocio
1. El formulario debe validar client-side campos requeridos (`email`, `password`).
2. En caso de credenciales inválidas, el backend devuelve 401 y la UI muestra el mensaje correspondiente.
3. Las pruebas UI deben ser idempotentes y no depender de datos creados por otras pruebas.

---

## 2. DISEÑO

### Modelos de Datos (en contexto de pruebas)
| Entidad | Descripción |
|---------|-------------|
| `Credentials` | Contenedor con `email` y `password` usado por Tasks y Fixtures de prueba |

### API Endpoints (referencia mínima que la UI usa)
| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/auth/login` | POST | Endpoint esperado para autenticación (se asume esquema JSON `{ email, password }`) |

> Nota: Si el backend real usa otra ruta o esquema, adaptar los Tasks de Screenplay para interceptar/mocked responses o usar el endpoint real.

### Diseño Frontend — Screenplay

#### Actores
- `Usuario` (Actor que realiza las tareas de signin)

#### Tasks (Responsabilidad única por Task)
- `OpenSignInPage` — Navega a `/signin`.
- `EnterCredentials` — Ingresa `email` y `password` (recibe un objeto `Credentials`).
- `SubmitLogin` — Envía el formulario de signin.
- `VerifySuccessfulLogin` — Verifica redirección y presencia de elemento de dashboard.
- `VerifyLoginError` — Verifica la aparición del mensaje de error con texto esperado.

#### Questions
- `IsOnDashboard` — Pregunta si el actor está en el dashboard.
- `LoginErrorMessage` — Recupera texto del mensaje de error para aserciones.

#### UI Targets (propuesta de selectores)
- `EMAIL_FIELD` — css: `input[name="email"]`
- `PASSWORD_FIELD` — css: `input[name="password"]`
- `SUBMIT_BUTTON` — css: `button[type="submit"]`
- `ERROR_MESSAGE` — css: `div.alert-error` (o `span.error` según implementación)
- `DASHBOARD_MARKER` — css: `div.dashboard` (elemento único del dashboard)

#### Estructura de paquetes (propuesta)
- `src/test/java/com/auto/ui/actors` — definición de actores
- `src/test/java/com/auto/ui/tasks` — Tasks (OpenSignInPage, EnterCredentials...)
- `src/test/java/com/auto/ui/questions` — Questions
- `src/test/java/com/auto/ui/ui` — Targets (UI mapping)
- `src/test/resources/features` — features Cucumber (`signin_screenplay.feature`)
- `src/test/java/com/auto/ui/steps` — Step Definitions / Glue

### Configuración y Dependencias
- `Gradle` con los siguientes artefactos (ejemplos):
  - `net.serenity-bdd:serenity-core`
  - `net.serenity-bdd:serenity-screenplay`
  - `net.serenity-bdd:serenity-screenplay-webdriver`
  - `net.serenity-bdd:serenity-junit5` o `serenity-junit` según runner
  - `io.cucumber:cucumber-java` y `io.cucumber:cucumber-junit`
  - `org.seleniumhq.selenium:selenium-java`

- `serenity.conf` (ubicado en `src/test/resources/serenity.conf`) — valores mínimos:
```
webdriver:
  driver: chrome
  base.url: "http://localhost:3001"

serenity:
  project.name: "AUTO_FRONT_SCREENPLAY"
```

- Ejecutar tests con Gradle: `./gradlew clean test` (o `./gradlew clean aggregate` si se usa plugin de Serenity para reports)

### Notas de Implementación
- Cada `Task` debe representar una sola acción (principio SRP).
- Usar `Target` para localizar elementos en lugar de `@FindBy` (Screenplay practice).
- Mantener los steps de Cucumber muy declarativos; la lógica de interacción debe vivir en Tasks.

---

## 3. LISTA DE TAREAS

### Frontend — Screenplay (Implementación)
- [ ] Crear feature Cucumber: `src/test/resources/features/signin_screenplay.feature`
- [ ] Definir Actor `Usuario` en `actors` package
- [ ] Implementar `ui` Targets (EMAIL_FIELD, PASSWORD_FIELD, SUBMIT_BUTTON, ERROR_MESSAGE, DASHBOARD_MARKER)
- [ ] Implementar Tasks: `OpenSignInPage`, `EnterCredentials`, `SubmitLogin`, `VerifySuccessfulLogin`, `VerifyLoginError`
- [ ] Implementar Questions: `IsOnDashboard`, `LoginErrorMessage`
- [ ] Crear Step Definitions / Glue que coordinen Actor ↔ Feature
- [ ] Configurar `serenity.conf` con `base.url: http://localhost:3001` y driver
- [ ] Añadir dependencias Serenity/Screenplay/Cucumber en `build.gradle`
- [ ] Añadir README con instrucciones de ejecución (`./gradlew clean test`) y precondiciones (app levantada en localhost:3001)
- [ ] Ejecutar pruebas localmente y verificar reportes en `target/site/serenity`

### QA / Gherkin / Riesgos
- [ ] Ejecutar `/gherkin-case-generator` para revisar y enriquecer escenarios
- [ ] Ejecutar `/risk-identifier` y documentar riesgos críticos (dependencia de backend, flakiness de UI)
- [ ] Validar que cada escenario sea independiente y idempotente

### Entrega
- [ ] Marcar spec como `status: APPROVED` antes de implementar
- [ ] Subir repositorio a GitHub: `AUTO_FRONT_SCREENPLAY`
- [ ] Incluir `README.md` con pasos para ejecutar y visualizar reportes

---

> Notas finales: Si la ruta de autenticación o los selectores del DOM difieren, actualizar los Targets y/o mockear las respuestas del endpoint `/api/auth/login` durante el desarrollo de las Tasks.

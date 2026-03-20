# AUTO_FRONT_SCREENPLAY

Proyecto de automatización UI con el patrón **Screenplay** sobre Serenity BDD, Cucumber y Gradle.  
Cubre dos escenarios independientes del flujo de autenticación de la aplicación en `http://localhost:3001`.

---

## Stack

| Herramienta | Versión |
|---|---|
| Java | 11+ |
| Gradle | 8.x |
| Serenity BDD | 3.9.8 |
| Cucumber | 7.11.1 |
| Chrome | última estable |

---

## Escenarios probados

| # | Tipo | Descripción |
|---|---|---|
| 1 | Flujo positivo | Inicio de sesión exitoso con credenciales válidas → redirige al dashboard |
| 2 | Flujo negativo | Inicio de sesión fallido con credenciales inválidas → muestra mensaje de error |

---

## Estructura del proyecto

```
src/
└── test/
    ├── java/com/auto/ui/
    │   ├── runners/     → CucumberTestSuite.java
    │   ├── steps/       → SignInStepDefinitions.java
    │   ├── tasks/       → OpenSignInPage / EnterCredentials / SubmitLogin
    │   ├── questions/   → IsOnDashboard / LoginErrorMessage
    │   └── ui/          → SignInPage (Targets)
    └── resources/
        ├── features/    → signin_screenplay.feature
        └── serenity.conf
```

---

## Precondiciones

1. **Java 11+** instalado y en `PATH`.
2. **Gradle 8.x** instalado (`gradle --version`).
3. **Google Chrome** instalado (la resolución del driver la gestiona Selenium Manager).
4. **Aplicación corriendo** en `http://localhost:3001` antes de ejecutar las pruebas.
5. **Actualizar credenciales** en `src/test/resources/features/signin_screenplay.feature`:
   - Credencial válida: reemplaza `test@correo.com` / `Test1234` con un usuario real.
   - Credencial inválida: puede dejarse igual o ajustarse.
6. **Ajustar selectores** en `src/test/java/com/auto/ui/ui/SignInPage.java` si los campos del formulario tienen otros atributos `name` o clases CSS.

---

## Ejecución

### Inicializar el wrapper de Gradle (solo primera vez)

```bash
gradle wrapper --gradle-version 8.7
```

### Ejecutar las pruebas

```bash
./gradlew clean test --no-daemon
```

### Ejecutar solo la suite de Cucumber

```bash
./gradlew clean test --tests com.auto.ui.runners.CucumberTestSuite --no-daemon
```

### Generar el reporte Serenity

```bash
./gradlew clean test aggregate
```

### Solo regenerar el reporte (sin volver a ejecutar tests)

```bash
./gradlew aggregate
```

---

## Visualizar el reporte

Después de ejecutar `aggregate`, abre en el navegador:

```
target/site/serenity/index.html
```

---

## Arquitectura Screenplay

```
Actor (Usuario)
  └─ attemptsTo →  Tasks
                    ├── OpenSignInPage   (navegar a /signin)
                    ├── EnterCredentials (ingresar email + password)
                    └── SubmitLogin      (enviar formulario)
  └─ should      →  Questions
                    ├── IsOnDashboard    (verifica URL post-login)
                    └── LoginErrorMessage (lee texto del error)
```

Cada `Task` tiene **una sola responsabilidad** (SRP).

---

## Ajuste rápido de selectores

Edita `src/test/java/com/auto/ui/ui/SignInPage.java` y actualiza los `locatedBy` según el DOM de tu aplicación:

| Target | Selector por defecto | Ajustar si... |
|---|---|---|
| `EMAIL_FIELD` | `input[type='email'], input[placeholder='Email'], input[placeholder='Correo electrónico']` | El campo usa otro tipo o placeholder |
| `PASSWORD_FIELD` | `input[type='password'], input[placeholder='Contraseña'], input[placeholder='Password']` | El campo usa otro tipo o placeholder |
| `SUBMIT_BUTTON` | `button[type='submit']` | El botón no es de tipo `submit` |
| `ERROR_MESSAGE` | `.alert-error, [data-testid='error-message'], .error-message, p.error, .text-red-500` | El error usa otra clase CSS |

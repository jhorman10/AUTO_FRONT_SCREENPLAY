package com.auto.ui.steps;

import com.auto.ui.questions.AppointmentStatusOf;
import com.auto.ui.questions.EmptyStateIsVisible;
import com.auto.ui.questions.QueuePositionOf;
import com.auto.ui.questions.WebSocketConnectionStatus;
import com.auto.ui.tasks.LoginAsReceptionist;
import com.auto.ui.tasks.NavigateToRegistration;
import com.auto.ui.tasks.NavigateToWaitingRoom;
import com.auto.ui.tasks.RegisterAppointment;
import com.auto.ui.tasks.WaitForQueueUpdate;
import com.auto.ui.utils.TestConstants;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.drawTheCurtain;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

public class QueuePositionStepDefinitions {

    private int registeredAppointmentCount = 0;
    private final List<String> registeredPatients = new ArrayList<>();

    @Before("@visualizacion-cola")
    public void prepararEscenario() {
        waitForApplicationAvailability();
        cleanupAppointments();
        setTheStage(new OnlineCast());

        theActorCalled(TestConstants.ACTOR_RECEPTIONIST);
        theActorCalled(TestConstants.ACTOR_PATIENT);
    }

    @After("@visualizacion-cola")
    public void cerrarNavegadores() {
        try {
            try {
                BrowseTheWeb.as(theActorCalled(TestConstants.ACTOR_RECEPTIONIST)).getDriver().quit();
            } catch (Exception ignored) {
            }

            try {
                BrowseTheWeb.as(theActorCalled(TestConstants.ACTOR_PATIENT)).getDriver().quit();
            } catch (Exception ignored) {
            }
        } finally {
            drawTheCurtain();
        }
    }

    private void cleanupAppointments() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "exec", "P1_mongodb", "mongosh",
                    "--username", "sofka_admin",
                    "--password", "sofka_secure_pass_456",
                    "--authenticationDatabase", "admin",
                    "appointments_db",
                    "--eval", "db.appointments.deleteMany({})"
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
        } catch (Exception ignored) {
            // Non-critical: tests may still pass if DB is clean
        }
        // Give the backend/WebSocket time to sync after cleanup
        LockSupport.parkNanos(1_000_000_000L); // 1 second
    }

    private void waitForApplicationAvailability() {
        String targetUrl = TestConstants.BASE_URL + TestConstants.WAITING_ROOM_PATH;
        long deadline = System.currentTimeMillis() + (TestConstants.APP_AVAILABILITY_TIMEOUT_SECONDS * 1000L);

        while (System.currentTimeMillis() < deadline) {
            if (isHttpReachable(targetUrl)) {
                return;
            }

            LockSupport.parkNanos(TestConstants.APP_AVAILABILITY_POLL_INTERVAL_MILLIS * 1_000_000L);
            if (Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Se interrumpió la espera de disponibilidad de la app");
            }
        }

        throw new IllegalStateException(
                "La aplicación no responde en " + targetUrl + " dentro de "
                + TestConstants.APP_AVAILABILITY_TIMEOUT_SECONDS + " segundos"
        );
    }

    private boolean isHttpReachable(String targetUrl) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) URI.create(targetUrl).toURL().openConnection();
            connection.setRequestMethod(TestConstants.HTTP_METHOD_GET);
            connection.setConnectTimeout(TestConstants.HTTP_CONNECT_TIMEOUT_MILLIS);
            connection.setReadTimeout(TestConstants.HTTP_READ_TIMEOUT_MILLIS);
            int statusCode = connection.getResponseCode();
            return statusCode >= TestConstants.HTTP_SUCCESS_STATUS_MIN
                    && statusCode < TestConstants.HTTP_SERVER_ERROR_STATUS_MIN;
        } catch (IOException ignored) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // ─── Background ─────────────────────────────────────────────
    @Given("la aplicación está disponible en la pantalla pública de sala de espera")
    public void laAplicacionEstaDisponible() {
        // La disponibilidad ya fue verificada en @Before
    }

    // ─── Given ──────────────────────────────────────────────────
    @Given("el recepcionista ha iniciado sesión en el sistema")
    public void elRecepcionistaHaIniciadoSesion() {
        theActorCalled(TestConstants.ACTOR_RECEPTIONIST).attemptsTo(
                LoginAsReceptionist.now()
        );
    }

    @Given("el recepcionista ha registrado un turno con urgencia {string} para el paciente {string} con cédula {string}")
    public void elRecepcionistaHaRegistradoUnTurno(String urgencia, String paciente, String cedula) {
        theActorCalled(TestConstants.ACTOR_RECEPTIONIST).attemptsTo(
                NavigateToRegistration.now(),
                RegisterAppointment.forPatient(paciente, cedula, urgencia)
        );
        registeredAppointmentCount++;
        registeredPatients.add(paciente);
    }

    @Given("el recepcionista ha registrado los siguientes turnos:")
    public void elRecepcionistaHaRegistradoLosSiguientesTurnos(DataTable dataTable) {
        List<Map<String, String>> turnos = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> turno : turnos) {
            theActorCalled(TestConstants.ACTOR_RECEPTIONIST).attemptsTo(
                    NavigateToRegistration.now(),
                    RegisterAppointment.forPatient(
                            turno.get("paciente"),
                            turno.get("cedula"),
                            turno.get("urgencia")
                    )
            );
            registeredAppointmentCount++;
            registeredPatients.add(turno.get("paciente"));
        }
    }

    @Given("el paciente {string} tiene posición visible en la pantalla de espera")
    public void elPacienteTienePosicionVisible(String paciente) {
        theActorCalled(TestConstants.ACTOR_PATIENT).attemptsTo(
                NavigateToWaitingRoom.now(),
                WaitForQueueUpdate.untilPatientVisible(paciente)
        );
    }

    @Given("no existen turnos activos en el sistema")
    public void noExistenTurnosActivos() {
        // Precondición implícita: no se registraron turnos en este escenario
    }

    // ─── When ───────────────────────────────────────────────────
    @When("el paciente consulta la pantalla pública de sala de espera")
    public void elPacienteConsultaLaPantalla() {
        theActorCalled(TestConstants.ACTOR_PATIENT).attemptsTo(
                NavigateToWaitingRoom.now()
        );

        if (!registeredPatients.isEmpty()) {
            String lastPatient = registeredPatients.get(registeredPatients.size() - 1);
            theActorCalled(TestConstants.ACTOR_PATIENT).attemptsTo(
                    WaitForQueueUpdate.untilPatientVisible(lastPatient)
            );
        }
    }

    @When("el recepcionista registra un nuevo turno con urgencia {string} para el paciente {string} con cédula {string}")
    public void elRecepcionistaRegistraUnNuevoTurno(String urgencia, String paciente, String cedula) {
        theActorCalled(TestConstants.ACTOR_RECEPTIONIST).attemptsTo(
                NavigateToRegistration.now(),
                RegisterAppointment.forPatient(paciente, cedula, urgencia)
        );
        registeredAppointmentCount++;
        registeredPatients.add(paciente);
    }

    // ─── Then ───────────────────────────────────────────────────
    @Then("la pantalla muestra el turno del paciente {string} en alguna sección")
    public void laPantallaMuestraElTurnoDelPaciente(String paciente) {
        theActorCalled(TestConstants.ACTOR_PATIENT).should(
                seeThat(QueuePositionOf.patient(paciente), is(greaterThan(0)))
        );
    }

    @Then("el turno del paciente {string} tiene un estado activo visible")
    public void elTurnoTieneEstadoActivoVisible(String paciente) {
        theActorCalled(TestConstants.ACTOR_PATIENT).should(
                seeThat(AppointmentStatusOf.patient(paciente), is(not(emptyOrNullString())))
        );
    }

    @Then("los siguientes pacientes aparecen en la pantalla:")
    public void losSiguientesPacientesAparecenEnLaPantalla(DataTable dataTable) {
        List<Map<String, String>> filas = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> fila : filas) {
            String paciente = fila.get("paciente");
            theActorCalled(TestConstants.ACTOR_PATIENT).should(
                    seeThat(QueuePositionOf.patient(paciente), is(greaterThan(0)))
            );
        }
    }

    @Then("la pantalla muestra un estado vacío sin errores visuales")
    public void laPantallaMuestraEstadoVacio() {
        theActorCalled(TestConstants.ACTOR_PATIENT).should(
                seeThat(EmptyStateIsVisible.onScreen(), is(true))
        );
    }

    @Then("el indicador de estado WebSocket está visible")
    public void elIndicadorWebSocketEstaVisible() {
        theActorCalled(TestConstants.ACTOR_PATIENT).should(
                seeThat(WebSocketConnectionStatus.displayed(), is(not(emptyOrNullString())))
        );
    }
}

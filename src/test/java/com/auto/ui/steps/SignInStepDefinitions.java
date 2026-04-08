package com.auto.ui.steps;

import com.auto.ui.questions.IsOnDashboard;
import com.auto.ui.questions.LoginErrorMessage;
import com.auto.ui.tasks.EnsureUserExists;
import com.auto.ui.tasks.EnterCredentials;
import com.auto.ui.tasks.OpenSignInPage;
import com.auto.ui.tasks.SubmitLogin;
import com.auto.ui.utils.TestConstants;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actors.OnlineCast;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.drawTheCurtain;
import static net.serenitybdd.screenplay.actors.OnStage.setTheStage;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.concurrent.locks.LockSupport;

public class SignInStepDefinitions {

    private String validEmail;
    private String validPassword;

    @Before
    public void prepararEscenario() {
        waitForApplicationAvailability();
        setTheStage(new OnlineCast());
        validEmail = TestConstants.DEFAULT_VALID_EMAIL;
        validPassword = TestConstants.DEFAULT_VALID_PASSWORD;
        theActorCalled(TestConstants.ACTOR_NAME);
    }

    @After
    public void cerrarNavegador() {
        try {
            BrowseTheWeb.as(theActorInTheSpotlight()).getDriver().quit();
        } finally {
            drawTheCurtain();
        }
    }

    private void waitForApplicationAvailability() {
        String targetUrl = TestConstants.BASE_URL + TestConstants.LOGIN_PATH;
        long deadline = System.currentTimeMillis() + (TestConstants.APP_AVAILABILITY_TIMEOUT_SECONDS * 1000L);

        while (System.currentTimeMillis() < deadline) {
            if (isHttpReachable(targetUrl)) {
                return;
            }

            LockSupport.parkNanos(TestConstants.APP_AVAILABILITY_POLL_INTERVAL_MILLIS * 1_000_000L);
            if (Thread.currentThread().isInterrupted()) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Se interrumpio la espera de disponibilidad de la app");
            }
        }

        throw new IllegalStateException(
                "La aplicacion no responde en " + targetUrl + " dentro de "
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

    @Given("el usuario se encuentra en la pantalla de inicio de sesión")
    public void elUsuarioSeEncuentraEnLaPantallaDeInicioSesion() {
        theActorInTheSpotlight().attemptsTo(
                OpenSignInPage.now()
        );
    }

    @When("el usuario ingresa el correo {string} y la contraseña {string}")
    public void elUsuarioIngresaCredenciales(String email, String password) {
        String effectiveEmail = email;
        String effectivePassword = password;

        if (TestConstants.DEFAULT_VALID_EMAIL.equals(email) && TestConstants.DEFAULT_VALID_PASSWORD.equals(password)) {
            effectiveEmail = validEmail;
            effectivePassword = validPassword;
        }

        theActorInTheSpotlight().attemptsTo(
                EnterCredentials.with(effectiveEmail, effectivePassword)
        );
    }

    @When("el usuario ingresa las credenciales {string}")
    public void elUsuarioIngresaCredencialesPorAlias(String credentialsAlias) {
        String normalizedAlias = credentialsAlias == null ? "" : credentialsAlias.trim().toUpperCase();

        switch (normalizedAlias) {
            case TestConstants.CREDENTIALS_ALIAS_VALIDAS:
                elUsuarioIngresaCredenciales(TestConstants.DEFAULT_VALID_EMAIL, TestConstants.DEFAULT_VALID_PASSWORD);
                break;
            case TestConstants.CREDENTIALS_ALIAS_INVALIDAS:
                elUsuarioIngresaCredenciales(TestConstants.DEFAULT_INVALID_EMAIL, TestConstants.DEFAULT_INVALID_PASSWORD);
                break;
            default:
                throw new IllegalArgumentException("Alias de credenciales no soportado: " + credentialsAlias);
        }
    }

    @When("envía el formulario de inicio de sesión")
    public void enviaElFormularioDeInicioSesion() {
        theActorInTheSpotlight().attemptsTo(
                SubmitLogin.form()
        );
    }

    @Then("el usuario accede exitosamente al dashboard")
    public void elUsuarioAccedeExitosamenteAlDashboard() {
        theActorInTheSpotlight().should(
                seeThat(IsOnDashboard.visible(), is(true))
        );
    }

    @Then("el sistema muestra un mensaje de error de autenticación")
    public void elSistemaMuestraUnMensajeDeErrorDeAutenticacion() {
        theActorInTheSpotlight().should(
                seeThat(LoginErrorMessage.displayed(), not(emptyOrNullString()))
        );
    }
}

package com.auto.ui.steps;

import com.auto.ui.questions.IsOnDashboard;
import com.auto.ui.questions.LoginErrorMessage;
import com.auto.ui.tasks.EnsureUserExists;
import com.auto.ui.tasks.EnterCredentials;
import com.auto.ui.tasks.OpenSignInPage;
import com.auto.ui.tasks.SubmitLogin;
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

public class SignInStepDefinitions {

    private String validEmail;
    private String validPassword;

    @Before
    public void prepararEscenario() {
        setTheStage(new OnlineCast());
        validEmail = "juan.perez@example.com";
        validPassword = "SecurePass123!";
        theActorCalled("Usuario").attemptsTo(
                EnsureUserExists.with("Usuario QA", validEmail, validPassword)
        );
    }

    @After
    public void cerrarNavegador() {
        try {
            BrowseTheWeb.as(theActorInTheSpotlight()).getDriver().quit();
        } finally {
            drawTheCurtain();
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

        if ("juan.perez@example.com".equals(email) && "SecurePass123!".equals(password)) {
            effectiveEmail = validEmail;
            effectivePassword = validPassword;
        }

        theActorInTheSpotlight().attemptsTo(
                EnterCredentials.with(effectiveEmail, effectivePassword)
        );
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

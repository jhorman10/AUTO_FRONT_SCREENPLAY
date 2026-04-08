package com.auto.ui.tasks;

import com.auto.ui.ui.RegistrationPage;
import com.auto.ui.utils.TestConstants;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterAppointment implements Task {

    private final String patientName;
    private final String idCard;
    private final String urgency;

    public RegisterAppointment(String patientName, String idCard, String urgency) {
        this.patientName = patientName;
        this.idCard = idCard;
        this.urgency = urgency;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        // Wait for form to be ready (not in loading state)
        new WebDriverWait(driver, Duration.ofSeconds(TestConstants.DASHBOARD_WAIT_TIMEOUT_SECONDS))
                .until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("input[placeholder='Nombre Completo']")
                ));

        actor.attemptsTo(
                Clear.field(RegistrationPage.PATIENT_NAME_FIELD),
                Enter.theValue(patientName).into(RegistrationPage.PATIENT_NAME_FIELD),
                Clear.field(RegistrationPage.ID_CARD_FIELD),
                Enter.theValue(idCard).into(RegistrationPage.ID_CARD_FIELD),
                SelectUrgency.withLevel(urgency),
                Click.on(RegistrationPage.SUBMIT_BUTTON)
        );

        // Wait for either success or error message to appear
        new WebDriverWait(driver, Duration.ofSeconds(TestConstants.DASHBOARD_WAIT_TIMEOUT_SECONDS))
                .until(d -> {
                    boolean hasSuccess = !d.findElements(By.cssSelector("[class*='success']")).isEmpty();
                    boolean hasError = !d.findElements(By.cssSelector("[class*='error']")).isEmpty();
                    return hasSuccess || hasError;
                });

        // If error, throw with the error text for diagnosis
        var errors = driver.findElements(By.cssSelector("[class*='error']"));
        if (!errors.isEmpty() && driver.findElements(By.cssSelector("[class*='success']")).isEmpty()) {
            String errorText = errors.get(0).getText();
            throw new AssertionError("Registration failed for '" + patientName + "': " + errorText);
        }
    }

    public static RegisterAppointment forPatient(String patientName, String idCard, String urgency) {
        return new RegisterAppointment(patientName, idCard, urgency);
    }
}

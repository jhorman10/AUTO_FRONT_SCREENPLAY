package com.auto.ui.tasks;

import com.auto.ui.utils.TestConstants;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NavigateToRegistration implements Task {

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(TestConstants.BASE_URL + TestConstants.REGISTRATION_PATH)
        );

        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        // Wait for registration form to render (requires auth state to resolve)
        new WebDriverWait(driver, Duration.ofSeconds(TestConstants.DASHBOARD_WAIT_TIMEOUT_SECONDS))
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("[class*='AppointmentRegistrationForm'] button, form button")
                ));
    }

    public static NavigateToRegistration now() {
        return new NavigateToRegistration();
    }
}

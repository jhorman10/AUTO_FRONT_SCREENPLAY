package com.auto.ui.tasks;

import com.auto.ui.utils.TestConstants;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginAsReceptionist implements Task {

    private static final Target EMAIL_INPUT
            = Target.the("campo email login")
                    .locatedBy("css:[data-testid='email-input']");

    private static final Target PASSWORD_INPUT
            = Target.the("campo password login")
                    .locatedBy("css:[data-testid='password-input']");

    private static final Target SUBMIT_BUTTON
            = Target.the("botón submit login")
                    .locatedBy("css:[data-testid='submit-button']");

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(TestConstants.BASE_URL + TestConstants.LOGIN_PATH)
        );

        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        // Wait for form to be interactive (inputs become enabled after JS hydration)
        new WebDriverWait(driver, Duration.ofSeconds(TestConstants.DASHBOARD_WAIT_TIMEOUT_SECONDS))
                .until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("[data-testid='email-input']")
                ));

        actor.attemptsTo(
                Enter.theValue(TestConstants.RECEPTIONIST_EMAIL).into(EMAIL_INPUT),
                Enter.theValue(TestConstants.RECEPTIONIST_PASSWORD).into(PASSWORD_INPUT),
                Click.on(SUBMIT_BUTTON)
        );

        // Wait for auth to settle — redirected away from login
        new WebDriverWait(driver, Duration.ofSeconds(TestConstants.DASHBOARD_WAIT_TIMEOUT_SECONDS))
                .until(d -> !d.getCurrentUrl().contains(TestConstants.LOGIN_PATH));
    }

    public static LoginAsReceptionist now() {
        return new LoginAsReceptionist();
    }
}

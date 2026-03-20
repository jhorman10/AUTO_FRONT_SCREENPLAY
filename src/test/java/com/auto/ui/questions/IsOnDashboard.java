package com.auto.ui.questions;

import java.time.Duration;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class IsOnDashboard implements Question<Boolean> {

    private static final By AUTHENTICATED_NAVIGATION = By.xpath(
            "//button[normalize-space()='Cerrar sesión'] | //a[@href='/dashboard' and normalize-space()='Dashboard']"
    );

    @Override
    public Boolean answeredBy(Actor actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        try {
            return new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(currentDriver -> {
                        String currentUrl = currentDriver.getCurrentUrl();
                        boolean hasAuthenticatedNavigation = !currentDriver.findElements(AUTHENTICATED_NAVIGATION).isEmpty();
                        boolean isOutsideSignIn = !currentUrl.contains("/signin") && !currentUrl.contains("/login");
                        return hasAuthenticatedNavigation || isOutsideSignIn;
                    });
        } catch (org.openqa.selenium.TimeoutException ignored) {
            return false;
        }
    }

    public static IsOnDashboard visible() {
        return new IsOnDashboard();
    }
}

package com.auto.ui.questions;

import com.auto.ui.utils.TestConstants;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class EmptyStateIsVisible implements Question<Boolean> {

    @Override
    public Boolean answeredBy(Actor actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        try {
            return new WebDriverWait(driver, Duration.ofSeconds(TestConstants.QUEUE_UPDATE_TIMEOUT_SECONDS))
                    .until(d -> !d.findElements(By.cssSelector(
                    "[class*='empty']"
            )).isEmpty());
        } catch (org.openqa.selenium.TimeoutException ignored) {
            return false;
        }
    }

    public static EmptyStateIsVisible onScreen() {
        return new EmptyStateIsVisible();
    }
}

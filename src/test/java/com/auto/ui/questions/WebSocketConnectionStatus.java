package com.auto.ui.questions;

import com.auto.ui.utils.TestConstants;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WebSocketConnectionStatus implements Question<String> {

    @Override
    public String answeredBy(Actor actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        try {
            new WebDriverWait(driver, Duration.ofSeconds(TestConstants.WEBSOCKET_CONNECT_TIMEOUT_SECONDS))
                    .until(d -> {
                        List<WebElement> indicators = d.findElements(By.cssSelector(
                                "[data-testid^='websocket-status-']"
                        ));
                        return !indicators.isEmpty();
                    });
        } catch (org.openqa.selenium.TimeoutException ignored) {
            return "";
        }

        List<WebElement> indicators = driver.findElements(By.cssSelector(
                "[data-testid^='websocket-status-']"
        ));

        if (!indicators.isEmpty()) {
            return indicators.get(0).getText().trim().toLowerCase();
        }

        return "";
    }

    public static WebSocketConnectionStatus displayed() {
        return new WebSocketConnectionStatus();
    }
}

package com.auto.ui.tasks;

import com.auto.ui.utils.TestConstants;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NavigateToWaitingRoom implements Task {

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(TestConstants.BASE_URL + TestConstants.WAITING_ROOM_PATH)
        );

        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        // Wait for WebSocket to connect (page renders appointment data client-side only)
        new WebDriverWait(driver, Duration.ofSeconds(TestConstants.WEBSOCKET_CONNECT_TIMEOUT_SECONDS))
                .until(d -> !d.findElements(By.cssSelector(
                "[data-testid^='websocket-status-connected']"
        )).isEmpty());
    }

    public static NavigateToWaitingRoom now() {
        return new NavigateToWaitingRoom();
    }
}

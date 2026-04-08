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

public class AppointmentStatusOf implements Question<String> {

    private final String patientName;

    public AppointmentStatusOf(String patientName) {
        this.patientName = patientName;
    }

    @Override
    public String answeredBy(Actor actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        String displayName = QueuePositionOf.anonymize(patientName);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(TestConstants.QUEUE_UPDATE_TIMEOUT_SECONDS))
                    .until(d -> {
                        List<WebElement> items = d.findElements(By.cssSelector(
                                "[class*='appointmentCard'], [class*='queueCard']"
                        ));
                        return items.stream().anyMatch(item
                                -> item.getText().contains(displayName) || item.getText().contains(patientName)
                        );
                    });
        } catch (org.openqa.selenium.TimeoutException ignored) {
            return "";
        }

        List<WebElement> items = driver.findElements(By.cssSelector(
                "[class*='appointmentCard'], [class*='queueCard']"
        ));

        for (WebElement item : items) {
            String text = item.getText();
            if (text.contains(displayName) || text.contains(patientName)) {
                String cssClass = item.getAttribute("class");
                if (cssClass.contains("waiting")) {
                    return "waiting";
                } else if (cssClass.contains("called")) {
                    return "called";
                } else if (cssClass.contains("completed")) {
                    return "completed";
                }
            }
        }

        return "";
    }

    public static AppointmentStatusOf patient(String patientName) {
        return new AppointmentStatusOf(patientName);
    }
}

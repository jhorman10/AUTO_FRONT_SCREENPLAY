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

public class QueuePositionOf implements Question<Integer> {

    private final String patientName;

    public QueuePositionOf(String patientName) {
        this.patientName = patientName;
    }

    /**
     * Converts "Carlos Gómez" → "Carlos G." to match the public page
     * anonymization.
     */
    public static String anonymize(String fullName) {
        String trimmed = fullName.trim();
        String[] parts = trimmed.split("\\s+");
        if (parts.length <= 1) {
            return trimmed;
        }
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            sb.append(' ').append(Character.toUpperCase(parts[i].charAt(0))).append('.');
        }
        return sb.toString();
    }

    @Override
    public Integer answeredBy(Actor actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        String displayName = anonymize(patientName);

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
            return -1;
        }

        List<WebElement> items = driver.findElements(By.cssSelector(
                "[class*='appointmentCard'], [class*='queueCard']"
        ));

        for (int i = 0; i < items.size(); i++) {
            String text = items.get(i).getText();
            if (text.contains(displayName) || text.contains(patientName)) {
                return i + 1;
            }
        }

        return -1;
    }

    public static QueuePositionOf patient(String patientName) {
        return new QueuePositionOf(patientName);
    }
}

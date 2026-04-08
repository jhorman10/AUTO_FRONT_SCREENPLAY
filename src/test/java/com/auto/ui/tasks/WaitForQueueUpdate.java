package com.auto.ui.tasks;

import com.auto.ui.questions.QueuePositionOf;
import com.auto.ui.utils.TestConstants;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WaitForQueueUpdate implements Task {

    private final int expectedSize;
    private final String patientName;

    private WaitForQueueUpdate(int expectedSize, String patientName) {
        this.expectedSize = expectedSize;
        this.patientName = patientName;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        new WebDriverWait(driver, Duration.ofSeconds(TestConstants.QUEUE_UPDATE_TIMEOUT_SECONDS))
                .until(d -> {
                    List<WebElement> items = d.findElements(By.cssSelector(
                            "[class*='appointmentCard'], [class*='queueCard']"
                    ));
                    if (patientName != null) {
                        String displayName = QueuePositionOf.anonymize(patientName);
                        return items.stream().anyMatch(item
                                -> item.getText().contains(displayName) || item.getText().contains(patientName)
                        );
                    }
                    return items.size() >= expectedSize;
                });
    }

    public static WaitForQueueUpdate untilQueueHasAtLeast(int size) {
        return new WaitForQueueUpdate(size, null);
    }

    public static WaitForQueueUpdate untilPatientVisible(String patientName) {
        return new WaitForQueueUpdate(1, patientName);
    }
}

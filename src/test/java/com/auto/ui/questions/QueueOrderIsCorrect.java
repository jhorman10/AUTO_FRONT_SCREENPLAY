package com.auto.ui.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueueOrderIsCorrect implements Question<Boolean> {

    private static final Map<String, Integer> URGENCY_WEIGHT = new HashMap<>();

    static {
        URGENCY_WEIGHT.put("alta", 1);
        URGENCY_WEIGHT.put("media", 2);
        URGENCY_WEIGHT.put("baja", 3);
    }

    private final List<String> expectedOrder;

    public QueueOrderIsCorrect(List<String> expectedOrder) {
        this.expectedOrder = expectedOrder;
    }

    @Override
    public Boolean answeredBy(Actor actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        List<WebElement> items = driver.findElements(By.cssSelector(
                "[class*='appointmentCard'], [class*='queueCard']"
        ));

        if (items.size() != expectedOrder.size()) {
            return false;
        }

        for (int i = 0; i < expectedOrder.size(); i++) {
            String itemText = items.get(i).getText();
            if (!itemText.contains(expectedOrder.get(i))) {
                return false;
            }
        }

        return true;
    }

    public static QueueOrderIsCorrect withPatients(String... patientNames) {
        return new QueueOrderIsCorrect(Arrays.asList(patientNames));
    }

    public static QueueOrderIsCorrect withPatients(List<String> patientNames) {
        return new QueueOrderIsCorrect(patientNames);
    }
}

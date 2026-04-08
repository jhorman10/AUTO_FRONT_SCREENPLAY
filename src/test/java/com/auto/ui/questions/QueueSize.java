package com.auto.ui.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class QueueSize implements Question<Integer> {

    @Override
    public Integer answeredBy(Actor actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();

        return driver.findElements(By.cssSelector(
                "[class*='appointmentCard'], [class*='queueCard']"
        )).size();
    }

    public static QueueSize value() {
        return new QueueSize();
    }
}

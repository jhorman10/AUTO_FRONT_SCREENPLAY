package com.auto.ui.questions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

public class IsOnDashboard implements Question<Boolean> {

    @Override
    public Boolean answeredBy(Actor actor) {
        String currentUrl = BrowseTheWeb.as(actor).getDriver().getCurrentUrl();
        return !currentUrl.contains("/signin") && !currentUrl.contains("/login");
    }

    public static IsOnDashboard visible() {
        return new IsOnDashboard();
    }
}

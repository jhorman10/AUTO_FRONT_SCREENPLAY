package com.auto.ui.tasks;

import com.auto.ui.utils.TestConstants;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;

public class OpenSignInPage implements Task {

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(TestConstants.BASE_URL + TestConstants.SIGN_IN_PATH)
        );
    }

    public static OpenSignInPage now() {
        return new OpenSignInPage();
    }
}

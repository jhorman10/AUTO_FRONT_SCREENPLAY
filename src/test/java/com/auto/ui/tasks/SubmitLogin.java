package com.auto.ui.tasks;

import com.auto.ui.ui.SignInPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;

public class SubmitLogin implements Task {

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Click.on(SignInPage.SUBMIT_BUTTON)
        );
    }

    public static SubmitLogin form() {
        return new SubmitLogin();
    }
}

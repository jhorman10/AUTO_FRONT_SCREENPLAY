package com.auto.ui.tasks;

import com.auto.ui.ui.SignInPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Enter;

public class EnterCredentials implements Task {

    private final String email;
    private final String password;

    public EnterCredentials(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Enter.theValue(email).into(SignInPage.EMAIL_FIELD),
                Enter.theValue(password).into(SignInPage.PASSWORD_FIELD)
        );
    }

    public static EnterCredentials with(String email, String password) {
        return new EnterCredentials(email, password);
    }
}

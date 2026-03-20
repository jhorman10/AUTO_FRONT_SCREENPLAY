package com.auto.ui.tasks;

import com.auto.ui.ui.SignUpPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Open;

public class EnsureUserExists implements Task {

    private final String fullName;
    private final String email;
    private final String password;

    public EnsureUserExists(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url("http://localhost:3001/signup"),
                Enter.theValue(fullName).into(SignUpPage.NAME_FIELD),
                Enter.theValue(email).into(SignUpPage.EMAIL_FIELD),
                Enter.theValue(password).into(SignUpPage.PASSWORD_FIELD),
                Click.on(SignUpPage.SUBMIT_BUTTON)
        );
    }

    public static EnsureUserExists with(String fullName, String email, String password) {
        return new EnsureUserExists(fullName, email, password);
    }
}
package com.auto.ui.questions;

import com.auto.ui.ui.SignInPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class LoginErrorMessage implements Question<String> {

    @Override
    public String answeredBy(Actor actor) {
        return SignInPage.ERROR_MESSAGE.resolveFor(actor).getText();
    }

    public static LoginErrorMessage displayed() {
        return new LoginErrorMessage();
    }
}

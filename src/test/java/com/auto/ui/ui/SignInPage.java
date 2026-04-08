package com.auto.ui.ui;

import net.serenitybdd.screenplay.targets.Target;

public class SignInPage {

    public static final Target EMAIL_FIELD
            = Target.the("campo de correo electrónico")
                    .locatedBy("css:[data-testid='email-input'], input[type='email']");

    public static final Target PASSWORD_FIELD
            = Target.the("campo de contraseña")
                    .locatedBy("css:[data-testid='password-input'], input[type='password']");

    public static final Target SUBMIT_BUTTON
            = Target.the("botón de inicio de sesión")
                    .locatedBy("css:[data-testid='submit-button'], button[type='submit']");

    public static final Target ERROR_MESSAGE
            = Target.the("mensaje de error de autenticación")
                    .locatedBy("css:p[role='alert'], [data-testid='error-message'], .error-message, p.error");
}

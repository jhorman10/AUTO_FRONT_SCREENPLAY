package com.auto.ui.ui;

import net.serenitybdd.screenplay.targets.Target;

public class SignInPage {

    public static final Target EMAIL_FIELD =
            Target.the("campo de correo electrónico")
                    .locatedBy("input[type='email'], input[placeholder='Email'], input[placeholder='Correo electrónico']");

    public static final Target PASSWORD_FIELD =
            Target.the("campo de contraseña")
                    .locatedBy("input[type='password'], input[placeholder='Contraseña'], input[placeholder='Password']");

    public static final Target SUBMIT_BUTTON =
            Target.the("botón de inicio de sesión")
                    .locatedBy("button[type='submit']");

    public static final Target ERROR_MESSAGE =
            Target.the("mensaje de error de autenticación")
                    .locatedBy(".alert-error, [data-testid='error-message'], .error-message, p.error, .text-red-500");
}

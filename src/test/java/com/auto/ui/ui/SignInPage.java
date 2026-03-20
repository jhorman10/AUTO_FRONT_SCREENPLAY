package com.auto.ui.ui;

import net.serenitybdd.screenplay.targets.Target;

public class SignInPage {

    public static final Target EMAIL_FIELD =
            Target.the("campo de correo electrónico")
                    .locatedBy("css:input[type='email'], input[placeholder='Email'], input[placeholder='Correo electrónico']");

    public static final Target PASSWORD_FIELD =
            Target.the("campo de contraseña")
                    .locatedBy("css:input[type='password'], input[placeholder='Contraseña'], input[placeholder='Password']");

    public static final Target SUBMIT_BUTTON =
            Target.the("botón de inicio de sesión")
                    .locatedBy("css:button[type='submit']");

    public static final Target ERROR_MESSAGE =
            Target.the("mensaje de error de autenticación")
                    .locatedBy("css:p[role='alert'], .SignInForm-module__AA7cza__error, .alert-error, [data-testid='error-message'], .error-message, p.error, .text-red-500");
}

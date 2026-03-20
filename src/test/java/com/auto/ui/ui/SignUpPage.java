package com.auto.ui.ui;

import net.serenitybdd.screenplay.targets.Target;

public class SignUpPage {

    public static final Target NAME_FIELD =
            Target.the("campo de nombre en registro")
                    .locatedBy("css:input[placeholder='Nombre']");

    public static final Target EMAIL_FIELD =
            Target.the("campo de correo en registro")
                    .locatedBy("css:input[type='email'], input[placeholder='Email']");

    public static final Target PASSWORD_FIELD =
            Target.the("campo de contraseña en registro")
                    .locatedBy("css:input[type='password'], input[placeholder='Contraseña']");

    public static final Target SUBMIT_BUTTON =
            Target.the("botón de registro")
                    .locatedBy("css:button[type='submit']");
}
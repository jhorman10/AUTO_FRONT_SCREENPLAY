package com.auto.ui.ui;

import net.serenitybdd.screenplay.targets.Target;

public class RegistrationPage {

    public static final Target PATIENT_NAME_FIELD
            = Target.the("campo nombre del paciente")
                    .locatedBy("css:input[placeholder='Nombre Completo']");

    public static final Target ID_CARD_FIELD
            = Target.the("campo cédula del paciente")
                    .locatedBy("css:input[placeholder*='Identificación']");

    public static final Target URGENCY_SELECT
            = Target.the("selector de urgencia")
                    .locatedBy("css:select[required]");

    public static final Target SUBMIT_BUTTON
            = Target.the("botón de registro de turno")
                    .locatedBy("css:[class*='AppointmentRegistrationForm'] button, form button");

    public static final Target SUCCESS_MESSAGE
            = Target.the("mensaje de registro exitoso")
                    .locatedBy("css:[data-testid='success-message'], .success, .alert-success, .success-message");
}

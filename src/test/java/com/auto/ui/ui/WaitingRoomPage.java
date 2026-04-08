package com.auto.ui.ui;

import net.serenitybdd.screenplay.targets.Target;

public class WaitingRoomPage {

    public static final Target QUEUE_LIST
            = Target.the("lista de turnos en cola de espera")
                    .locatedBy("css:[class*='waitingQueue'], [class*='sectionBlock']");

    public static final Target QUEUE_ITEM
            = Target.the("elemento de turno en la cola")
                    .locatedBy("css:[class*='appointmentCard'], [class*='queueCard']");

    public static final Target QUEUE_POSITION
            = Target.the("posición del turno en la cola")
                    .locatedBy("css:[data-testid='queue-position-badge']");

    public static final Target QUEUE_STATUS
            = Target.the("estado del turno")
                    .locatedBy("css:[class*='statusBadge']");

    public static final Target PATIENT_NAME
            = Target.the("nombre del paciente en la tarjeta")
                    .locatedBy("css:[class*='nombre']");

    public static final Target URGENCY_BADGE
            = Target.the("indicador de urgencia del turno")
                    .locatedBy("css:[class*='badge']:not([class*='countBadge']):not([class*='WebSocketStatus'])");

    public static final Target WEBSOCKET_STATUS
            = Target.the("indicador de estado de conexión WebSocket")
                    .locatedBy("css:[data-testid^='websocket-status-']");

    public static final Target EMPTY_STATE
            = Target.the("mensaje de cola vacía")
                    .locatedBy("css:[class*='empty']");
}

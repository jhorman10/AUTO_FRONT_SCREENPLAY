package com.auto.ui.utils;

public final class TestConstants {

    private TestConstants() {
    }

    public static final String BASE_URL = "http://localhost:3001";
    public static final String SIGN_IN_PATH = "/signin";
    public static final String LOGIN_PATH = "/login";
    public static final String SIGN_UP_PATH = "/signup";
    public static final String DASHBOARD_PATH = "/dashboard";
    public static final String WAITING_ROOM_PATH = "/";
    public static final String REGISTRATION_PATH = "/registration";

    public static final String ACTOR_NAME = "Usuario";
    public static final String ACTOR_RECEPTIONIST = "Recepcionista";
    public static final String ACTOR_PATIENT = "Paciente";
    public static final String DEFAULT_FULL_NAME = "Usuario QA";
    public static final String DEFAULT_VALID_EMAIL = "recepcion@clinica.com";
    public static final String DEFAULT_VALID_PASSWORD = "Recep.2026!";
    public static final String DEFAULT_INVALID_EMAIL = "invalido@correo.com";
    public static final String DEFAULT_INVALID_PASSWORD = "ClaveErronea";
    public static final String RECEPTIONIST_EMAIL = "recepcion@clinica.com";
    public static final String RECEPTIONIST_PASSWORD = "Recep.2026!";
    public static final String CREDENTIALS_ALIAS_VALIDAS = "VALIDAS";
    public static final String CREDENTIALS_ALIAS_INVALIDAS = "INVALIDAS";

    public static final String URGENCY_ALTA = "Alta";
    public static final String URGENCY_MEDIA = "Media";
    public static final String URGENCY_BAJA = "Baja";
    public static final String STATUS_WAITING = "esperando";

    public static final int DASHBOARD_WAIT_TIMEOUT_SECONDS = 15;
    public static final int APP_AVAILABILITY_TIMEOUT_SECONDS = 30;
    public static final long APP_AVAILABILITY_POLL_INTERVAL_MILLIS = 1000L;
    public static final int QUEUE_UPDATE_TIMEOUT_SECONDS = 15;
    public static final int WEBSOCKET_CONNECT_TIMEOUT_SECONDS = 10;
    public static final String HTTP_METHOD_GET = "GET";
    public static final int HTTP_CONNECT_TIMEOUT_MILLIS = 2000;
    public static final int HTTP_READ_TIMEOUT_MILLIS = 2000;
    public static final int HTTP_SUCCESS_STATUS_MIN = 200;
    public static final int HTTP_SERVER_ERROR_STATUS_MIN = 500;
}

package com.auto.ui.utils;

public final class TestConstants {

    private TestConstants() {
    }

    public static final String BASE_URL = "http://localhost:3001";
    public static final String SIGN_IN_PATH = "/signin";
    public static final String LOGIN_PATH = "/login";
    public static final String SIGN_UP_PATH = "/signup";
    public static final String DASHBOARD_PATH = "/dashboard";

    public static final String ACTOR_NAME = "Usuario";
    public static final String DEFAULT_FULL_NAME = "Usuario QA";
    public static final String DEFAULT_VALID_EMAIL = "juan.perez@example.com";
    public static final String DEFAULT_VALID_PASSWORD = "SecurePass123!";
    public static final String DEFAULT_INVALID_EMAIL = "invalido@correo.com";
    public static final String DEFAULT_INVALID_PASSWORD = "ClaveErronea";
    public static final String CREDENTIALS_ALIAS_VALIDAS = "VALIDAS";
    public static final String CREDENTIALS_ALIAS_INVALIDAS = "INVALIDAS";

    public static final int DASHBOARD_WAIT_TIMEOUT_SECONDS = 10;
    public static final int APP_AVAILABILITY_TIMEOUT_SECONDS = 30;
    public static final long APP_AVAILABILITY_POLL_INTERVAL_MILLIS = 1000L;
    public static final String HTTP_METHOD_GET = "GET";
    public static final int HTTP_CONNECT_TIMEOUT_MILLIS = 2000;
    public static final int HTTP_READ_TIMEOUT_MILLIS = 2000;
    public static final int HTTP_SUCCESS_STATUS_MIN = 200;
    public static final int HTTP_SERVER_ERROR_STATUS_MIN = 500;
}

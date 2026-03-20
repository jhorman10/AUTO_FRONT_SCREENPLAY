package com.auto.ui.utils;

public final class TestConstants {

    private TestConstants() {
    }

    public static final String BASE_URL = "http://localhost:3001";
    public static final String SIGN_IN_PATH = "/signin";
    public static final String SIGN_UP_PATH = "/signup";

    public static final String ACTOR_NAME = "Usuario";
    public static final String DEFAULT_FULL_NAME = "Usuario QA";
    public static final String DEFAULT_VALID_EMAIL = "juan.perez@example.com";
    public static final String DEFAULT_VALID_PASSWORD = "SecurePass123!";

    public static final int DASHBOARD_WAIT_TIMEOUT_SECONDS = 10;
    public static final int APP_AVAILABILITY_TIMEOUT_SECONDS = 30;
    public static final long APP_AVAILABILITY_POLL_INTERVAL_MILLIS = 1000L;
}

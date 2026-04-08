package com.auto.ui.tasks;

import com.auto.ui.ui.RegistrationPage;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.SelectFromOptions;

import java.util.HashMap;
import java.util.Map;

public class SelectUrgency implements Task {

    private static final Map<String, String> URGENCY_MAP = new HashMap<>();

    static {
        URGENCY_MAP.put("alta", "high");
        URGENCY_MAP.put("media", "medium");
        URGENCY_MAP.put("baja", "low");
    }

    private final String level;

    public SelectUrgency(String level) {
        this.level = level;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String value = URGENCY_MAP.getOrDefault(level.toLowerCase(), level.toLowerCase());
        actor.attemptsTo(
                SelectFromOptions.byValue(value).from(RegistrationPage.URGENCY_SELECT)
        );
    }

    public static SelectUrgency withLevel(String level) {
        return new SelectUrgency(level);
    }
}

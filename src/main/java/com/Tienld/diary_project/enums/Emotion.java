package com.Tienld.diary_project.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Emotion {
    POSITIVE("Tích cực 😊"),
    NEGATIVE("Tiêu cực 😢"),
    NEUTRAL("Bình thường 😐");

    private final String description;

    Emotion(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static final Map<String, String> MAP = Stream.of(values()).collect(Collectors.toMap(Enum::name, Emotion::getDescription));
}
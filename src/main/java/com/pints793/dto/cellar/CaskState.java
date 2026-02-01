package com.pints793.dto.cellar;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CaskState {
    DELIVERED("delivered"),
    RACKED("racked"),
    SETTLED("settled"),
    VENTED("vented"),
    NEEDS_TAP("needs_tap"),
    TAPPED("tapped"),
    READY_TO_SERVE("ready_to_serve"),
    PULLING("pulling"),
    TIRED("tired");

    CaskState(String value) {
        this.value = value;
    }

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    public CaskState parse(String state) {
        return switch (state) {
            case "delivered" -> DELIVERED;
            case "racked" -> RACKED;
            case "settled" -> SETTLED;
            case "vented" -> VENTED;
            case "needs_tap" -> NEEDS_TAP;
            case "tapped" -> TAPPED;
            case "ready_to_serve" -> READY_TO_SERVE;
            case "pulling" -> PULLING;
            case "tired" -> TIRED;
            default -> null;
        };
    }
}

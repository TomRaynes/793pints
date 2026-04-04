package com.pints793.cask;

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


    public static CaskState parse(String state) {
        if (state == null) {
            return null;
        }
        return CaskState.valueOf(state.toUpperCase().replace(' ', '_'));
    }

    public boolean hasCooldown() {
        return equals(RACKED) || equals(VENTED) || equals(TAPPED) || equals(PULLING);
    }

    public CaskState getNextState() {
        return ordinal() < TIRED.ordinal() ? values()[ordinal() + 1] : this;
    }
}

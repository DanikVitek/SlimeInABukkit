package com.danikvitek.slimeinabukkit.config;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum SoundMode {
    OFF,
    ONCE,
    LOOP;

    @Contract(pure = true)
    public static SoundMode fromString(@NotNull String string) {
        return switch (string.toLowerCase(Locale.ROOT)) {
            case "off" -> OFF;
            case "once" -> ONCE;
            case "loop" -> LOOP;
            default -> throw new IllegalArgumentException("Unknown sound mode: " + string);
        };
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return switch (this) {
            case OFF -> "off";
            case ONCE -> "once";
            case LOOP -> "loop";
        };
    }
}

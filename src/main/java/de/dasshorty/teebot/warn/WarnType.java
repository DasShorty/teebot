package de.dasshorty.teebot.warn;

import lombok.Getter;

public enum WarnType {

    SWEARING("Beleidigungen"), // 1h mute
    MISBEHAVIOR("Fehlverhalten"), // 1h mute
    NOISES("Störgeräusche");


    @Getter
    private final String reason;

    WarnType(String reason) {

        this.reason = reason;
    }
}

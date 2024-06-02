package de.dasshorty.teebot.warn;

import lombok.Getter;

@Getter
enum WarnPunishment {

    NONE(0, 3),
    MUTE(1, 3),
    TIMEOUT(2, 3);


    private final int weight;
    private final int count;

    WarnPunishment(int weight, int count) {
        this.weight = weight;
        this.count = count;
    }

    public static WarnPunishment getNextPunishment(WarnPunishment punishment) {

        int i = punishment.weight + 1;

        for (WarnPunishment p : WarnPunishment.values()) {
            if (p.count == i) {
                return p;
            }
        }

        return punishment;
    }
}

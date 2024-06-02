package de.dasshorty.teebot.warn;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class WarnDto {

    private WarnType type;
    private String punisherId;
    private long time;
    private long submitted;
    private WarnPunishment punishment;

}

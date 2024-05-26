package de.dasshorty.teebot.warn;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarnDto {

    private WarnType type;
    private String punisherId;
}

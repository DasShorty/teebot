package de.dasshorty.teebot.warn;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@Setter
public class UserWarnDto {

    @Id
    private String userId;
    private List<WarnDto> warns;


}

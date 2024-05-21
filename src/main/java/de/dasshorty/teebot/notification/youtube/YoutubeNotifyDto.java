package de.dasshorty.teebot.notification.youtube;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class YoutubeNotifyDto {

    @Id
    private String id;
    private String youtubeVideoId = null;

}

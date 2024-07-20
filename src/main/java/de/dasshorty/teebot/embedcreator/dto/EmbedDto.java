package de.dasshorty.teebot.embedcreator.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


/*
 * https://embed.dan.onl/
 * */
@Getter
@Setter
@Document(collection = "embeds")
public class EmbedDto {
    long timestamp;
    @Id
    private String embedId;
    private String embedDescription;
    private Author author;
    private String title;
    private String url;
    private String description;
    private List<Field> fields;
    private Image image;
    private Image thumbnail;
    private String color;
    private Footer footer;
}

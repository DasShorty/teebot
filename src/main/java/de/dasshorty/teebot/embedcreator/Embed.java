package de.dasshorty.teebot.embedcreator;

import de.dasshorty.teebot.api.ToGson;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class Embed implements ToGson {

    private String embedId;
    private Author author;
    private Content content;
    private Fields fields;
    private Footer footer;
    private Style style;

    public Embed(String embedId, Author author, Content content, Fields fields, Footer footer, Style style) {
        this.embedId = embedId;
        this.author = author;
        this.content = content;
        this.fields = fields;
        this.footer = footer;
        this.style = style;
    }

    public String getEmbedId() {
        return this.embedId;
    }

    public void setEmbedId(String embedId) {
        this.embedId = embedId;
    }

    public Author getAuthor() {
        return this.author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Content getContent() {
        return this.content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Fields getFields() {
        return this.fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public Footer getFooter() {
        return this.footer;
    }

    public void setFooter(Footer footer) {
        this.footer = footer;
    }

    public Style getStyle() {
        return this.style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    @SuppressWarnings("OverlyLongMethod")
    public EmbedBuilder buildEmbed() {

        EmbedBuilder sendEmbed = new EmbedBuilder();

        String title = this.content.title();
        if (null != title)
            sendEmbed.setTitle(title);

        String description = this.content.description();
        if (null != description)
            sendEmbed.setDescription(description);

        String authorText = this.author.authorText();
        if (null != authorText)
            sendEmbed.setAuthor(authorText);

        String authorLink = this.author.authorLink();
        if (null != authorText && null != authorLink)
            sendEmbed.setAuthor(authorText, authorLink);

        String authorImage = this.author.authorImage();
        if (null != authorText && null != authorLink && null != authorImage)
            sendEmbed.setAuthor(authorText, authorLink, authorImage);

        List<Field> fields = this.fields.fields();
        if (fields != null)
            fields.forEach(field -> sendEmbed.addField(field.fieldTitle(), field.fieldContent(), false));

        Style style = this.style;

        String thumbnail = style.thumbnail();
        if (thumbnail != null)
            sendEmbed.setThumbnail(thumbnail);

        String image = style.image();
        if (image != null)
            sendEmbed.setImage(image);

        String color = style.color();
        if (color != null) {

            Color embedColor = Color.WHITE;

            try {
                embedColor = Color.decode(color);
            } catch (NumberFormatException exception) {
                exception.fillInStackTrace();
                return new EmbedBuilder()
                        .setAuthor("Embed System")
                        .setColor(Color.RED)
                        .setDescription("Die angegebene Farbe " + color + " ist nicht als Farbe registriert!");
            }

            sendEmbed.setColor(embedColor);
        }

        Long timestamp = style.timestamp();
        if (timestamp != null)
            sendEmbed.setTimestamp(Instant.ofEpochMilli(timestamp.longValue()));

        String footerText = this.getFooter().footerText();
        if (footerText != null)
            sendEmbed.setFooter(footerText);

        return sendEmbed;
    }

    public record Author(String authorText, String authorImage, String authorLink) implements ToGson {

    }

    public record Content(String title, String description) implements ToGson {

    }

    public record Style(String color, String image, String thumbnail, Long timestamp) implements ToGson {

    }

    public record Fields(List<Field> fields) implements ToGson {

    }

    public record Field(String fieldTitle, String fieldContent, boolean inline) implements ToGson {

    }

    public record Footer(String footerText) implements ToGson {

    }

}

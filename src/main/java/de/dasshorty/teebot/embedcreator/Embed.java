package de.dasshorty.teebot.embedcreator;

import de.dasshorty.teebot.api.ToGson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Embed implements ToGson {

    private String embedId;
    private Author author;
    private Content content;
    private Fields fields;
    private Footer footer;
    private Style style;

   @SuppressWarnings("OverlyLongMethod")
   public EmbedBuilder buildEmbed() {

        val sendEmbed = new EmbedBuilder();

        val title = this.content.title();
        if (null != title)
            sendEmbed.setTitle(title);

        val description = this.content.description();
        if (null != description)
            sendEmbed.setDescription(description);

        val authorText = this.author.authorText();
        if (null != authorText)
            sendEmbed.setAuthor(authorText);

        val authorLink = this.author.authorLink();
        if (null != authorText && null != authorLink)
            sendEmbed.setAuthor(authorText, authorLink);

        val authorImage = this.author.authorImage();
        if (null != authorText && null != authorLink && null != authorImage)
            sendEmbed.setAuthor(authorText, authorLink, authorImage);

        val fields = this.fields.fields();
        if (fields != null)
            fields.forEach(field -> sendEmbed.addField(field.fieldTitle(), field.fieldContent(), false));

        val style = this.style;

        val thumbnail = style.thumbnail();
        if (thumbnail != null)
            sendEmbed.setThumbnail(thumbnail);

        val image = style.image();
        if (image != null)
            sendEmbed.setImage(image);

        val color = style.color();
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

        val timestamp = style.timestamp();
        if (timestamp != null)
            sendEmbed.setTimestamp(Instant.ofEpochMilli(timestamp.longValue()));

        val footerText = this.getFooter().footerText();
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

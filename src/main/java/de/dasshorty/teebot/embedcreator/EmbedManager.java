package de.dasshorty.teebot.embedcreator;

import de.dasshorty.teebot.DiscordBot;
import de.dasshorty.teebot.api.DiscordManager;
import de.dasshorty.teebot.embedcreator.dto.Author;
import de.dasshorty.teebot.embedcreator.dto.EmbedDto;
import de.dasshorty.teebot.embedcreator.dto.Footer;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Getter
public class EmbedManager implements DiscordManager {

    private final EmbedRepository embedRepo;

    public EmbedManager(EmbedRepository embedRepo) {
        this.embedRepo = embedRepo;
    }

    private EmbedBuilder convertDto(EmbedDto dto) {

        EmbedBuilder builder = new EmbedBuilder();

        if (dto.getAuthor() != null) {

            Author author = dto.getAuthor();
            builder.setAuthor(author.name(), author.url(), author.icon_url());

        }

        if (dto.getTitle() != null)
            builder.setTitle(dto.getTitle());

        if (dto.getColor() != null)
            builder.setColor(Color.getColor(dto.getColor()));

        if (dto.getDescription() != null)
            builder.setDescription(dto.getDescription());

        if (dto.getFields() != null) {

            dto.getFields().forEach(field -> {
                builder.addField(field.name(), field.value(), field.inline());
            });

        }

        if (dto.getFooter() != null) {

            Footer footer = dto.getFooter();
            builder.setFooter(footer.text(), footer.icon_url());

        }
        if (dto.getImage() != null)
            builder.setImage(dto.getImage().url());

        if (dto.getThumbnail() != null)
            builder.setThumbnail(dto.getThumbnail().url());

        if (dto.getTimestamp() <= 0)
            builder.setTimestamp(new Date(dto.getTimestamp()).toInstant());

        return builder;
    }

    public EmbedBuilder buildEmbed(String id) {

        Optional<EmbedDto> optional = this.embedRepo.getEmbedDtoByEmbedId(id);

        return optional.map(this::convertDto).orElse(null);

    }

    public List<EmbedDto> filteredByQuery(String query) {
        return this.embedRepo.getEmbedDtosByEmbedDescriptionContaining(query);
    }

    @Override
    public void setupDiscord(DiscordBot bot) {

    }
}

package de.dasshorty.teebot.embedcreator.steps.step4.image;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.modal.Modal;
import de.dasshorty.teebot.embedcreator.Embed;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

@RequiredArgsConstructor
public class ImageModal implements Modal {

    private final EmbedDatabase embedDatabase;

    @Override
    public String id() {
        return "embed-creator-add-image";
    }

    @Override
    public void onExecute(ModalInteractionEvent event) {
        val member = event.getMember();
        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN) || Roles.hasMemberRole(member, Roles.DEVELOPER))) {
            event.reply("Du hast keine Rechte für diese Aktion!")
                    .setEphemeral(true).queue();
            return;
        }

        val embedId = this.embedDatabase.getMemberEmbedMap().get(member.getId());

        event.deferReply(true).queue();

        val optionalEmbed = this.embedDatabase.getEmbed(embedId);

        if (optionalEmbed.isEmpty()) {
            event.getHook().editOriginal("Etwas ist schiefgelaufen! Bitte Melde dies an <@&1159086761763414046> (ImageModal.java:45)").queue();
            return;
        }

        val embed = optionalEmbed.get();

        val url = Objects.requireNonNull(event.getValue("url")).getAsString();

        val style = embed.getStyle();

        embed.setStyle(new Embed.Style(style.color(), url, style.thumbnail(), style.timestamp()));

        this.embedDatabase.storeEmbed(embed);

        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor("Embed Creator")
                        .setDescription("Bild wurde gesetzt!")
                        .setColor(Color.WHITE)
                        .setTimestamp(Instant.now())
                        .setFooter("Step 4 / 4")
                        .build())
                .queue(message -> this.embedDatabase.getMemberEmbedMap().put(member.getId(), embedId));
    }
}

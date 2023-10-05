package de.dasshorty.teebot.embedcreator.steps.step1;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.modal.Modal;
import de.dasshorty.teebot.embedcreator.Embed;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

@RequiredArgsConstructor
public class AuthorModal implements Modal {

    private final EmbedDatabase embedDatabase;

    @Override
    public String id() {
        return "embed-builder-autor";
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

        event.deferReply(true).queue();

        val author = Objects.requireNonNull(event.getValue("author")).getAsString();
        val image = Objects.requireNonNull(event.getValue("image")).getAsString();
        val url = Objects.requireNonNull(event.getValue("url")).getAsString();

        val embedId = this.embedDatabase.getMemberEmbedMap().get(member.getId());

        val optionalEmbed = this.embedDatabase.getEmbed(embedId);

        if (optionalEmbed.isEmpty()) {
            event.getHook().editOriginal("Etwas ist schiefgelaufen! Bitte Melde dies an <@&1159086761763414046> (AutorModal.java:45)").queue();
            return;
        }

        val embed = optionalEmbed.get();

        embed.setAuthor(new Embed.Author(author, image, url));

        this.embedDatabase.storeEmbed(embed);

        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor("Embed Creator")
                        .setDescription("Der Autor wurde erfolgreich geändert!")
                        .setColor(Color.WHITE)
                        .setTimestamp(Instant.now())
                        .setFooter("Step 1 / 4")
                        .build())
                .queue(message -> this.embedDatabase.getMemberEmbedMap().put(member.getId(), embedId));

    }
}

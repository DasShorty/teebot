package de.dasshorty.teebot.embedcreator.steps.step2;

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
import java.util.ArrayList;
import java.util.Objects;

@RequiredArgsConstructor
public class FieldModal implements Modal {

    private final EmbedDatabase embedDatabase;

    @Override
    public String id() {
        return "embed-creator-step2";
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

        val embedId = this.embedDatabase.getMemberEmbedMap().get(member.getId());

        val optionalEmbed = this.embedDatabase.getEmbed(embedId);

        if (optionalEmbed.isEmpty()) {
            event.getHook().editOriginal("Etwas ist schiefgelaufen! Bitte Melde dies an <@&1159086761763414046> (FieldModal.java:39)").queue();
            return;
        }

        val embed = optionalEmbed.get();

        val title = Objects.requireNonNull(event.getValue("title")).getAsString();
        val description = Objects.requireNonNull(event.getValue("description")).getAsString();

        val fields = new ArrayList<>(embed.getFields().fields());

        fields.add(new Embed.Field(title, description, true));

        embed.setFields(new Embed.Fields(fields));

        this.embedDatabase.storeEmbed(embed);

        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor("Embed Creator")
                        .setDescription("Das Feld wurde erfolgreich hinzugefügt")
                        .setColor(Color.WHITE)
                        .setTimestamp(Instant.now())
                        .setFooter("Step 2 / 4")
                        .build())
                .queue(message -> this.embedDatabase.getMemberEmbedMap().put(member.getId(), embedId));

    }
}

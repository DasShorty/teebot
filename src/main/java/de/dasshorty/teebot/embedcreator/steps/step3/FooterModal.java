package de.dasshorty.teebot.embedcreator.steps.step3;

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
public class FooterModal implements Modal {

    private final EmbedDatabase embedDatabase;

    @Override
    public String id() {
        return "embed-creator-step3";
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
            event.getHook().editOriginal("Etwas ist schiefgelaufen! Bitte Melde dies an <@&1159086761763414046> (FooterModal.java:49)").queue();
            return;
        }

        val embed = optionalEmbed.get();

        val footer = Objects.requireNonNull(event.getValue("footer")).getAsString();

        embed.setFooter(new Embed.Footer(footer));

        this.embedDatabase.storeEmbed(embed);

        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor("Embed Creator")
                        .setDescription("Der Footer wurde erfolgreich hinzugefügt.")
                        .setColor(Color.WHITE)
                        .setTimestamp(Instant.now())
                        .setFooter("Step 3 / 4")
                        .build())
                .queue(message -> this.embedDatabase.getMemberEmbedMap().put(member.getId(), embedId));


    }
}

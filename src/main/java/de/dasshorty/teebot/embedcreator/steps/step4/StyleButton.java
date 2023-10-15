package de.dasshorty.teebot.embedcreator.steps.step4;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.awt.*;
import java.time.Instant;

public class StyleButton implements Button {
    private final EmbedDatabase embedDatabase;

    public StyleButton(EmbedDatabase embedDatabase) {
        this.embedDatabase = embedDatabase;
    }

    @Override
    public String id() {
        return "embed-creator-step4";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        Member member = event.getMember();
        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN) || Roles.hasMemberRole(member, Roles.DEVELOPER))) {
            event.reply("Du hast keine Rechte für diese Aktion!")
                    .setEphemeral(true).queue();
            return;
        }


        event.deferReply(true).queue();

        String embedId = this.embedDatabase.getMemberEmbedMap().get(member.getId());

        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor("Embed Creator")
                        .setDescription("Füge noch weitere Elemente hinzu")
                        .setColor(Color.WHITE)
                        .setTimestamp(Instant.now())
                        .setFooter("Step 4 / 4")
                        .build())
                .setComponents(ActionRow.of(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.primary("embed-creator-add-thumbnail", "Thumbnail hinzufügen"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.primary("embed-creator-add-image", "Bild hinzufügen"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.primary("embed-creator-add-timestamp", "Timestamp hinzufügen"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.primary("embed-creator-add-color", "Farbe hinzufügen"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("embed-creator-step5", "Embed speichern")
                ))
                .queue(message -> this.embedDatabase.getMemberEmbedMap().put(member.getId(), embedId));

    }
}

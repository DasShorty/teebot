package de.dasshorty.teebot.embedcreator.steps.step2;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;


public class FieldButton implements Button {

    private final EmbedDatabase embedDatabase;

    public FieldButton(EmbedDatabase embedDatabase) {
        this.embedDatabase = embedDatabase;
    }

    @Override
    public String id() {
        return "embed-creator-step2";
    }

    @Override
    public void onExecute(@NotNull ButtonInteractionEvent event) {

        Member member = event.getMember();
        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN) || Roles.hasMemberRole(member, Roles.DEVELOPER))) {
            event.reply("Du hast keine Rechte für diese Aktion!")
                    .setEphemeral(true).queue();
            return;
        }

        String embedId = this.embedDatabase.getMemberEmbedMap().get(member.getId());

        event.deferReply(true).queue();

        event.getHook().editOriginalEmbeds(
                        new EmbedBuilder()
                                .setAuthor("Embed Creator")
                                .setDescription("Möchtest du Felder hinzufügen?")
                                .setColor(Color.WHITE)
                                .setTimestamp(Instant.now())
                                .setFooter("Step 2 / 4")
                                .build())
                .setComponents(ActionRow.of(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.primary("embed-creator-add-field", "Feld hinzufügen"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("embed-creator-step3", "Fortfahren mit Footer (3/4)")
                ))
                .queue(message -> this.embedDatabase.getMemberEmbedMap().put(member.getId(), embedId));

    }
}

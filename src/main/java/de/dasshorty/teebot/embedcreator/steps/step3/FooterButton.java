package de.dasshorty.teebot.embedcreator.steps.step3;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.awt.*;
import java.time.Instant;

@RequiredArgsConstructor
public class FooterButton implements Button {

    private final EmbedDatabase embedDatabase;

    @Override
    public String id() {
        return "embed-creator-step3";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        val member = event.getMember();
        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN) || Roles.hasMemberRole(member, Roles.DEVELOPER))) {
            event.reply("Du hast keine Rechte für diese Aktion!")
                    .setEphemeral(true).queue();
            return;
        }

        val embedId = this.embedDatabase.getMemberEmbedMap().get(member.getId());

        event.deferReply(true).queue();

        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                .setAuthor("Embed Creator")
                .setDescription("Möchtest du einen Footer hinzufügen?")
                .setColor(Color.WHITE)
                .setTimestamp(Instant.now())
                .setFooter("Step 3 / 4")
                .build())
                .setComponents(ActionRow.of(
                        net.dv8tion.jda.api.interactions.components.buttons.Button.primary("embed-creator-add-footer", "Footer hinzufügen"),
                        net.dv8tion.jda.api.interactions.components.buttons.Button.secondary("embed-creator-step4", "Fortfahren mit Style (4/4)")
                ))
                .queue(message -> this.embedDatabase.getMemberEmbedMap().put(member.getId(), embedId));

    }
}

package de.dasshorty.teebot.jtc.button;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.jtc.JTC;
import de.dasshorty.teebot.jtc.JTCDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;
import java.util.Optional;

public class ChangeTitleButton implements Button {
    private final JTCDatabase jtcDatabase;

    public ChangeTitleButton(JTCDatabase jtcDatabase) {
        this.jtcDatabase = jtcDatabase;
    }

    @Override
    public String id() {
        return "jtc-change-title";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        Member member = event.getMember();

        String channelId = event.getChannel().getId();

        Optional<JTC> optionalJTC = this.jtcDatabase.getJTC(channelId);

        if (optionalJTC.isEmpty()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Voicechannel Controller")
                    .setDescription("Der Titel konnte nicht geändert werden")
                    .setColor(Color.RED)
                    .build()).setEphemeral(true).queue();
            return;
        }

        JTC jtc = optionalJTC.get();

        assert member != null;

        if (!jtc.channelOwner().equals(member.getId()) && !Roles.hasMemberRole(member, Roles.ADMIN, Roles.DEVELOPER, Roles.STAFF, Roles.FAMILIY)) {

            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Voicechannel Controller")
                    .setDescription("Du hast keine Berechtigung diesen Titel zu ändern!")
                    .setColor(Color.RED)
                    .build()).setEphemeral(true).queue();

            return;
        }

        Modal modal = Modal.create("jtc-enter-title", "Title ändern")
                .addActionRow(TextInput.create("title", "Titel", TextInputStyle.SHORT)
                        .setRequiredRange(5, 50)
                        .setRequired(true)
                        .setPlaceholder(event.getChannel().getName())
                        .build()).build();

        event.replyModal(modal).queue();
    }
}

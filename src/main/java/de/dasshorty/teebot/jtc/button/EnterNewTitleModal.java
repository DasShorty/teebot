package de.dasshorty.teebot.jtc.button;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.modal.Modal;
import de.dasshorty.teebot.jtc.JTC;
import de.dasshorty.teebot.jtc.JTCDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.awt.*;
import java.util.Optional;

public class EnterNewTitleModal implements Modal {
    private final JTCDatabase jtcDatabase;

    public EnterNewTitleModal(JTCDatabase jtcDatabase) {
        this.jtcDatabase = jtcDatabase;
    }

    @Override
    public String id() {
        return "jtc-enter-title";
    }

    @Override
    public void onExecute(ModalInteractionEvent event) {


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

        String title = event.getValue("title").getAsString();

        assert member != null;

        if (!jtc.channelOwner().equals(member.getId()) && !Roles.hasMemberRole(member, Roles.ADMIN, Roles.DEVELOPER, Roles.HEAD_MODERATOR, Roles.MODERATOR)) {

            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Voicechannel Controller")
                    .setDescription("Du hast keine Berechtigung diesen Titel zu ändern!")
                    .setColor(Color.RED)
                    .build()).setEphemeral(true).queue();

            return;
        }

        event.getChannel().asVoiceChannel().getManager().setName(title).queue();

        event.replyEmbeds(new EmbedBuilder()
                .setAuthor("Voicechannel Controller")
                .setDescription("Der Titel wurde erfolgreich geändert!")
                .setColor(Color.GREEN)
                .build()).queue();

    }
}

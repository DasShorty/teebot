package de.dasshorty.teebot.jtc.button;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.modal.Modal;
import de.dasshorty.teebot.jtc.JTC;
import de.dasshorty.teebot.jtc.JTCDto;
import de.dasshorty.teebot.jtc.JTCRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.awt.*;
import java.util.Optional;

public class EnterNewTitleModal implements Modal {
    private final JTCRepository jtcRepo;

    public EnterNewTitleModal(JTCRepository jtcRepository) {
        this.jtcRepo = jtcRepository;
    }

    @Override
    public String id() {
        return "jtc-enter-title";
    }

    @Override
    public void onExecute(ModalInteractionEvent event) {


        Member member = event.getMember();

        String channelId = event.getChannel().getId();

        Optional<JTCDto> optional = this.jtcRepo.findById(channelId);

        if (optional.isEmpty()) {
            event.replyEmbeds(new EmbedBuilder()
                    .setAuthor("Voicechannel Controller")
                    .setDescription("Der Titel konnte nicht geändert werden")
                    .setColor(Color.RED)
                    .build()).setEphemeral(true).queue();
            return;
        }

        JTCDto dto = optional.get();

        String title = event.getValue("title").getAsString();

        assert member != null;

        if (!dto.getChannelOwnerId().equals(member.getId()) && !Roles.hasMemberRole(member, Roles.ADMIN, Roles.DEVELOPER, Roles.STAFF)) {

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

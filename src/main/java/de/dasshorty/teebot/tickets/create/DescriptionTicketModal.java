package de.dasshorty.teebot.tickets.create;

import de.dasshorty.teebot.api.modal.Modal;
import de.dasshorty.teebot.tickets.TicketCache;
import de.dasshorty.teebot.tickets.TicketDto;
import de.dasshorty.teebot.tickets.TicketReason;
import de.dasshorty.teebot.tickets.TicketRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.types.ObjectId;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class DescriptionTicketModal implements Modal {

    private final TicketRepository ticketRepo;

    public DescriptionTicketModal(TicketRepository ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    @Override
    public String id() {
        return "ticket-create";
    }

    @Override
    public void onExecute(ModalInteractionEvent event) {

        Member member = event.getMember();

        event.deferReply(true).queue();

        String ticketDescription = event.getValue("description").getAsString();

        TicketReason reason = TicketCache.TICKET_REASON_CACHE.get(member);

        InteractionHook hook = event.getHook();

        if (reason == null) {
            hook.editOriginal("Wie es scheint hast du keine Kategorie ausgewählt!").queue();
            return;
        }

        TicketCache.TICKET_REASON_CACHE.remove(member);

        TextChannel ticketChannel = Objects.requireNonNull(event.getGuild()).getTextChannelById("836107968659456000");

        String ticketId = new ObjectId().toHexString();

        assert ticketChannel != null;

        this.sendTicketNotification(event.getGuild(), reason, ticketId);

        ticketChannel.createThreadChannel(ticketId, true).queue(threadChannel -> {

            assert member != null;

            // stop inviting random users
            threadChannel.getManager().setInvitable(false).setSlowmode(3).queue();

            threadChannel.sendMessage(member.getAsMention()).addEmbeds(new EmbedBuilder().setAuthor("Tickets").setDescription("Ein Teammitglied wird sich demnächst um dich kümmern. Falls du dein Problem noch nicht fertig Beschreiben konntest, kannst du nun auch Bilder & Videos mit in den Text Kanal schicken.").setColor(Color.white)

                    .addField("Ticketbeschreibung", ticketDescription, false).addField(":warning: Erinnerung", "Alle Nachrichten in diesem Kanal werden gespeichert, d.h. wir speichern deinen Namen (auf dem Disocrd Server), die Discord Mitglied's ID und die Nachricht. Du hast automatisch beim Akzeptieren der Regeln dies Akzeptiert.", false)

                    .setTimestamp(Instant.now()).build()).addActionRow(Button.danger("ticket-close", "Ticket schließen"), Button.secondary("ticket-add-team", "Weitere Teammitglieder anfragen")).queue();

            this.ticketRepo.save(new TicketDto(ticketId, member.getId(), threadChannel.getId(), reason, ticketDescription, List.of()));
            hook.editOriginal("Dein Ticket wurde in " + threadChannel.getAsMention() + " erstellt!").queue();

        });


    }

    private void sendTicketNotification(Guild guild, TicketReason providedReason, String ticketId) {
        TextChannel notificationChannel = guild.getTextChannelById("1207082628021358593");

        assert notificationChannel != null;
        notificationChannel.sendMessage("<@&835975805699751956>").addEmbeds(new EmbedBuilder().setAuthor("Tickets").setColor(Color.ORANGE).setDescription("Ein Teammitglied ist bei dem Ticket gefragt.").addField("Grund", providedReason.getReason(), true).addField("Id", String.valueOf(ticketId), true).setTimestamp(Instant.now()).build()).addActionRow(Button.primary("ticket-claim", "Ticket bearbeiten")).queue();

    }
}

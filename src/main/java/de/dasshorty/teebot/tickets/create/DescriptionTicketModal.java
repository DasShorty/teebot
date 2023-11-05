package de.dasshorty.teebot.tickets.create;

import de.dasshorty.teebot.api.modal.Modal;
import de.dasshorty.teebot.tickets.Ticket;
import de.dasshorty.teebot.tickets.TicketDatabase;
import de.dasshorty.teebot.tickets.TicketReason;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DescriptionTicketModal implements Modal {

    private final TicketDatabase ticketDatabase;

    public DescriptionTicketModal(TicketDatabase ticketDatabase) {
        this.ticketDatabase = ticketDatabase;
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

        Optional<TicketReason> optionalTicketReason = this.ticketDatabase.getTicketReason(member);

        InteractionHook hook = event.getHook();

        if (optionalTicketReason.isEmpty()) {
            hook.editOriginal("Wie es scheint hast du keine Kategorie ausgewählt!").queue();
            return;
        }

        this.ticketDatabase.removeFromTicketCache(member);

        TicketReason ticketReason = optionalTicketReason.get();

        Category category = Objects.requireNonNull(event.getGuild()).getCategoryById("1170764881721561178");

        long ticketId = this.ticketDatabase.getCreatedTickets() + 1L;

        assert category != null;
        category.createTextChannel(String.valueOf(ticketId)).queue(textChannel -> {
            TextChannelManager manager = textChannel.getManager();

            assert member != null;
            manager.putMemberPermissionOverride(member.getIdLong(), List.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND, Permission.MESSAGE_ATTACH_FILES), List.of()).queue();

            textChannel.sendMessage(member.getAsMention()).addEmbeds(new EmbedBuilder()
                            .setAuthor("Tickets")
                            .setDescription("Ein Teammitglied wird sich demnächst um dich kümmern. Falls du dein Problem noch nicht fertig Beschreiben konntest, kannst du nun auch Bilder & Videos mit in den Text Kanal schicken.")
                            .setColor(Color.white)

                            .addField("Ticketbeschreibung", ticketDescription, false)
                            .addField(":warning: Erinnerung", "Alle Nachrichten in diesem Kanal werden gespeichert, d.h. wir speichern deinen Namen (auf dem Disocrd Server), die Discord Mitglied's ID und die Nachricht. Du hast automatisch beim Akzeptieren der Regeln dies Akzeptiert.", false)

                            .setTimestamp(Instant.now())
                            .build())
                    .addActionRow(Button.primary("ticket-close", "Ticket schließen"),
                            Button.secondary("ticket-history", "Transcript anzeigen")
                    )
                    .queue();

            this.ticketDatabase.insertTicket(new Ticket(ticketId, member.getId(), textChannel.getId(), ticketReason, ticketDescription, List.of()));
        });


    }
}

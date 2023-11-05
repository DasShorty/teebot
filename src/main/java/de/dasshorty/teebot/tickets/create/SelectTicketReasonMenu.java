package de.dasshorty.teebot.tickets.create;

import de.dasshorty.teebot.api.menu.string.StringSelectionMenu;
import de.dasshorty.teebot.tickets.TicketDatabase;
import de.dasshorty.teebot.tickets.TicketReason;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class SelectTicketReasonMenu implements StringSelectionMenu {

    private final TicketDatabase ticketDatabase;

    public SelectTicketReasonMenu(TicketDatabase ticketDatabase) {
        this.ticketDatabase = ticketDatabase;
    }

    @Override
    public String id() {
        return "ticket-create";
    }

    @Override
    public void onExecute(StringSelectInteractionEvent event) {

        Member member = event.getMember();

        event.replyModal(Modal.create("ticket-create", "Ticket Erstellen")
                .addActionRow(TextInput.create("description", "Beschreibung", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Beschreibe dein Anliegen")
                        .setMinLength(100)
                        .setMaxLength(1000)
                        .setRequired(true)
                        .build())
                .build()).queue();

        String ticketReasonId = event.getSelectedOptions().get(0).getValue();

        this.ticketDatabase.addToTicketCache(member, TicketReason.valueOf(ticketReasonId));

    }
}

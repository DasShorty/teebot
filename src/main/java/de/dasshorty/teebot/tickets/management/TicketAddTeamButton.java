package de.dasshorty.teebot.tickets.management;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.tickets.TicketDto;
import de.dasshorty.teebot.tickets.TicketReason;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.Objects;
import java.util.Optional;

public class TicketAddTeamButton implements Button {

    private final TicketDatabase ticketDatabase;

    public TicketAddTeamButton(TicketDatabase ticketDatabase) {
        this.ticketDatabase = ticketDatabase;
    }

    @Override
    public String id() {
        return "ticket-add-team";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {


        Member member = event.getMember();

        assert member != null;
        if (!Roles.hasMemberRole(member, Roles.STAFF, Roles.ADMIN, Roles.DEVELOPER)) {
            event.reply("Du kannst keine Teammitglieder anfragen!").setEphemeral(true).queue();
            return;
        }


        event.deferReply(true).queue();

        InteractionHook hook = event.getHook();


        try {
            long ticketIdFromName = Long.parseLong(event.getChannel().getName());


            Optional<TicketDto> optionalTicket = this.ticketDatabase.getTicketWithId(ticketIdFromName);

            if (optionalTicket.isEmpty()) {
                hook.editOriginal("Dieser Kanal scheint kein Ticket zu sein.").queue();
                return;
            }


            TicketDatabase.sendTicketNotification(Objects.requireNonNull(event.getGuild()), TicketReason.NORMAL, ticketIdFromName);

            hook.editOriginal("Du hast weitere Teammitglieder für das Ticket angefragt!").queue();

        } catch (NumberFormatException e) {
            hook.editOriginal("Die Ticket ID konnte nicht richtig erfasst werden!").queue();
        }


    }
}

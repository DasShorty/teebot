package de.dasshorty.teebot.tickets.management;

import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.tickets.Ticket;
import de.dasshorty.teebot.tickets.TicketDatabase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TicketClaimButton implements Button {

    private final TicketDatabase ticketDatabase;

    public TicketClaimButton(TicketDatabase ticketDatabase) {
        this.ticketDatabase = ticketDatabase;
    }

    @Override
    public String id() {
        return "ticket-claim";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        Member member = event.getMember();

        Message message = event.getMessage();

        if (message.getEmbeds().isEmpty()) {
            event.reply("Es konnte keine ID gefunden werden!").setEphemeral(true).queue();
            return;
        }

        List<MessageEmbed.Field> fields = message.getEmbeds().get(0).getFields();

        if (fields.size() != 2) {
            event.reply("Es konnte keine ID gefunden werden!").setEphemeral(true).queue();
            return;
        }

        event.deferReply(true).queue();

        String ticketId = fields.get(1).getValue();

        assert ticketId != null;
        Optional<Ticket> optionalTicket = this.ticketDatabase.getTicketWithId(Long.parseLong(ticketId));

        InteractionHook hook = event.getHook();

        if (optionalTicket.isEmpty()) {
            hook.editOriginal("Es konnte kein Ticket mit der angegebenen Id gefunden werden!").queue();
            return;
        }

        Ticket ticket = optionalTicket.get();

        ThreadChannel threadTicketChannel = Objects.requireNonNull(event.getGuild()).getThreadChannelById(ticket.threadId());

        assert member != null;

        if (threadTicketChannel == null) {
            hook.editOriginal("Es konnte kein Kanal gefunden werden!").queue();
            return;
        }

        hook.editOriginal("Du wurdest zum Ticket hinzugefügt!").queue(ignored -> {

            event.getMessage()
                    .editMessageComponents(List.of(
                            ActionRow.of(
                                    net.dv8tion.jda.api.interactions.components.buttons.Button.primary("ticket-claim", "Das Ticket wurde bereits angenommen").asDisabled()
                            )
                    )).queue();

        });
    }
}

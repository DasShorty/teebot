package de.dasshorty.teebot.tickets.management;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.tickets.Ticket;
import de.dasshorty.teebot.tickets.TicketDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TicketCloseButton implements Button {

    private final TicketDatabase ticketDatabase;

    public TicketCloseButton(TicketDatabase ticketDatabase) {
        this.ticketDatabase = ticketDatabase;
    }

    @Override
    public String id() {
        return "ticket-close";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        Member member = event.getMember();

        assert member != null;
        if (!Roles.hasMemberRole(member, Roles.STAFF, Roles.ADMIN, Roles.DEVELOPER)) {
            event.reply("Du kannst das Ticket nicht schließen!").setEphemeral(true).queue();
            return;
        }


        event.deferReply(true).queue();

        InteractionHook hook = event.getHook();


        try {

            long idAsChannelName = Long.parseLong(event.getChannel().getName());


            Optional<Ticket> optionalTicket = this.ticketDatabase.getTicketWithId(idAsChannelName);

            if (optionalTicket.isEmpty() || event.getChannel().getType() != ChannelType.GUILD_PRIVATE_THREAD) {
                hook.editOriginal("Dieser Kanal scheint kein Ticket zu sein.").queue();
                return;
            }

            Ticket ticket = optionalTicket.get();

            ThreadChannel ticketChannel = event.getChannel().asThreadChannel();
            this.sendTicketClosed(ticketChannel);

            hook.editOriginal("Das Ticket wird in 1 Minuten gelöscht!").queue(message -> {
                event.getChannel().delete().queueAfter(1L, TimeUnit.MINUTES);

                ticketChannel.getManager().setLocked(true)
                        .setInvitable(false)
                        .setArchived(true).queue();

            });


        } catch (NumberFormatException e) {
            hook.editOriginal("Die Ticket ID konnte nicht richtig erfasst werden!").queue();
        }

    }

    private void sendTicketClosed(ThreadChannel threadChannel) {

        threadChannel.sendMessageEmbeds(new EmbedBuilder()
                .setAuthor("Tickets")
                .setColor(Color.ORANGE)
                .setDescription("Das Ticket wurde geschlossen!")
                .addField("Löschung in", "Das Ticket wird gelöscht <t:" + (Instant.now().getEpochSecond() + Duration.ofMinutes(1L).toSeconds()) + ":R>", false)
                .setTimestamp(Instant.now())
                .build()).queue();

    }
}

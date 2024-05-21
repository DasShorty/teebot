package de.dasshorty.teebot.tickets.management;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.tickets.TicketDto;
import de.dasshorty.teebot.tickets.TicketReason;
import de.dasshorty.teebot.tickets.TicketRepository;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class TicketAddTeamButton implements Button {

    private final TicketRepository ticketRepo;

    public TicketAddTeamButton(TicketRepository ticketRepo) {
        this.ticketRepo = ticketRepo;
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

            Optional<TicketDto> optional = this.ticketRepo.findByThreadId(event.getChannel().getId());

            if (optional.isEmpty()) {
                hook.editOriginal("Dieser Kanal scheint kein Ticket zu sein.").queue();
                return;
            }


            this.sendTicketNotification(Objects.requireNonNull(event.getGuild()), TicketReason.NORMAL, optional.get().getTicketId());

            hook.editOriginal("Du hast weitere Teammitglieder für das Ticket angefragt!").queue();

        } catch (NumberFormatException e) {
            hook.editOriginal("Die Ticket ID konnte nicht richtig erfasst werden!").queue();
        }


    }


    private void sendTicketNotification(Guild guild, TicketReason providedReason, String ticketId) {
        TextChannel notificationChannel = guild.getTextChannelById("1207082628021358593");

        assert notificationChannel != null;
        notificationChannel.sendMessage("<@&835975805699751956>").addEmbeds(new EmbedBuilder().setAuthor("Tickets").setColor(Color.ORANGE).setDescription("Ein Teammitglied ist bei dem Ticket gefragt.").addField("Grund", providedReason.getReason(), true).addField("Id", String.valueOf(ticketId), true).setTimestamp(Instant.now()).build()).addActionRow(net.dv8tion.jda.api.interactions.components.buttons.Button.primary("ticket-claim", "Ticket bearbeiten")).queue();

    }

}

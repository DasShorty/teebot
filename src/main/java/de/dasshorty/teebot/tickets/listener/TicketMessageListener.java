package de.dasshorty.teebot.tickets.listener;

import de.dasshorty.teebot.tickets.TicketDto;
import de.dasshorty.teebot.tickets.TicketMessageData;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Optional;

public class TicketMessageListener extends ListenerAdapter {

    private final TicketDatabase ticketDatabase;

    public TicketMessageListener(TicketDatabase ticketDatabase) {
        this.ticketDatabase = ticketDatabase;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getChannelType() != ChannelType.GUILD_PRIVATE_THREAD)
            return;

        ThreadChannel threadChannel = event.getChannel().asThreadChannel();

        if (!threadChannel.getParentMessageChannel().getId().equals("1159846560549576817"))
            return;

        long ticketId = Long.parseLong(threadChannel.getName());

        Optional<TicketDto> optionalTicket = this.ticketDatabase.getTicketWithId(ticketId);

        if (optionalTicket.isEmpty())
            return;

        TicketDto ticketDto = optionalTicket.get();

        ArrayList<TicketMessageData> messages = new ArrayList<>(ticketDto.messages());

        Member member = event.getMember();
        assert member != null;

        messages.add(new TicketMessageData(member.getId(), member.getEffectiveName(), event.getChannel().getId(), String.valueOf(ticketId), event.getMessage().getContentRaw()));

        TicketDto updatedTicketDto = new TicketDto(ticketId, ticketDto.opener(), ticketDto.threadId(), ticketDto.reason(), ticketDto.description(), messages);

        this.ticketDatabase.updateTicket(updatedTicketDto);
    }
}

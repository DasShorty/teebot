package de.dasshorty.teebot.tickets.listener;

import de.dasshorty.teebot.tickets.TicketDto;
import de.dasshorty.teebot.tickets.TicketMessageData;
import de.dasshorty.teebot.tickets.TicketRepository;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Optional;

public class TicketMessageListener extends ListenerAdapter {

    private final TicketRepository ticketRepo;

    public TicketMessageListener(TicketRepository ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getChannelType() != ChannelType.GUILD_PRIVATE_THREAD)
            return;

        ThreadChannel threadChannel = event.getChannel().asThreadChannel();

        if (!threadChannel.getParentMessageChannel().getId().equals("1159846560549576817"))
            return;


        Optional<TicketDto> optional = this.ticketRepo.findByThreadId(threadChannel.getId());

        if (optional.isEmpty())
            return;

        TicketDto ticketDto = optional.get();

        ArrayList<TicketMessageData> messages = new ArrayList<>(ticketDto.getMessages());

        Member member = event.getMember();
        assert member != null;

        messages.add(new TicketMessageData(member.getId(), member.getEffectiveName(), event.getChannel().getId(), ticketDto.getTicketId(), event.getMessage().getContentRaw()));
        ticketDto.setMessages(messages);

        this.ticketRepo.save(ticketDto);
    }
}

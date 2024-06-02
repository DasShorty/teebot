package de.dasshorty.teebot.tickets;

import de.dasshorty.teebot.DiscordBot;
import de.dasshorty.teebot.api.DiscordManager;
import de.dasshorty.teebot.tickets.create.CreateTicketButton;
import de.dasshorty.teebot.tickets.create.DescriptionTicketModal;
import de.dasshorty.teebot.tickets.create.SelectTicketReasonMenu;
import de.dasshorty.teebot.tickets.listener.TicketMessageListener;
import de.dasshorty.teebot.tickets.management.TicketAddTeamButton;
import de.dasshorty.teebot.tickets.management.TicketClaimButton;
import de.dasshorty.teebot.tickets.management.TicketCloseButton;

public class TicketManager implements DiscordManager {

    private final TicketRepository ticketRepository;

    public TicketManager(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public void setupDiscord(DiscordBot bot) {
        bot.getApiHandler().addSlashCommand(new TicketCommand(this.ticketRepository));

        // create tickets
        bot.getApiHandler().addButton(new CreateTicketButton());
        bot.getApiHandler().addModal(new DescriptionTicketModal(this.ticketRepository));
        bot.getApiHandler().addStringMenu(new SelectTicketReasonMenu(this.ticketRepository));

        bot.getBuilder().addEventListeners(new TicketMessageListener(this.ticketRepository));

        bot.getApiHandler().addButton(new TicketAddTeamButton(this.ticketRepository));
        bot.getApiHandler().addButton(new TicketClaimButton(this.ticketRepository));
        bot.getApiHandler().addButton(new TicketCloseButton(this.ticketRepository));
    }
}

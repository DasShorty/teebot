package de.dasshorty.teebot.tickets;

import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;
import java.util.Map;

public class TicketCache {

    public static final Map<Member, TicketReason> TICKET_REASON_CACHE = new HashMap<>();

}

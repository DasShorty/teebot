package de.dasshorty.teebot.tickets;

import com.google.gson.Gson;
import org.bson.Document;

import java.util.List;

public record Ticket(long ticketId, String opener, String threadId, TicketReason reason, String description,
                     List<TicketMessageData> messages) {

    public static Ticket fromJson(String json) {
        return new Gson().fromJson(json, Ticket.class);
    }

    public Document toDocument() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), Document.class);
    }

}

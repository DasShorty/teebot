package de.dasshorty.teebot.tickets;

import com.google.gson.Gson;

public record TicketMessageData(String memberId, String memberName, String channelId, String channelName,
                                String messageContent) {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}

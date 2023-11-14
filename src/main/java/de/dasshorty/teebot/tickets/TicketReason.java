package de.dasshorty.teebot.tickets;

public enum TicketReason {

    NORMAL("General"),
    BLOCK_APPEAL("Entsperrung");

    private final String reason;

    TicketReason(String reason) {

        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }
}

package de.dasshorty.teebot.tickets;

public enum TicketReason {

    NORMAL("General", ":credit_card:"),
    BLOCK_APPEAL("Entsperrung", ":closed_lock_with_key:");

    private final String reason;
    private final String emojiCode;

    TicketReason(String reason, String emojiCode) {

        this.reason = reason;
        this.emojiCode = emojiCode;
    }

    public String getEmojiCode() {
        return this.emojiCode;
    }

    public String getReason() {
        return this.reason;
    }
}

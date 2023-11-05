package de.dasshorty.teebot.tickets;

import com.mongodb.client.MongoCollection;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import net.dv8tion.jda.api.entities.Member;
import org.bson.BsonObjectId;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TicketDatabase {

    private final MongoHandler mongoHandler;
    private final Map<Member, TicketReason> ticketCache = new HashMap<>();

    public TicketDatabase(MongoHandler mongoHandler) {
        this.mongoHandler = mongoHandler;
    }

    public void addToTicketCache(Member member, TicketReason reason) {
        this.ticketCache.put(member, reason);
    }

    public void removeFromTicketCache(Member member) {
        this.ticketCache.remove(member);
    }

    public Optional<TicketReason> getTicketReason(Member member) {

        if (!this.ticketCache.containsKey(member))
            return Optional.empty();

        return Optional.of(this.ticketCache.get(member));
    }

    private MongoCollection<Document> collection() {
        return this.mongoHandler.collection("tickets");
    }

    public long getCreatedTickets() {
        return this.collection().countDocuments();
    }

    public BsonObjectId insertTicket(Ticket ticket) {
        return Objects.requireNonNull(this.collection().insertOne(ticket.toDocument()).getInsertedId()).asObjectId();
    }

}

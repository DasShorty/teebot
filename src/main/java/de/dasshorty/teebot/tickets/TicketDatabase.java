package de.dasshorty.teebot.tickets;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.BsonObjectId;
import org.bson.Document;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class TicketDatabase {

    private final MongoHandler mongoHandler;
    private final Map<Member, TicketReason> ticketCache = new HashMap<>();

    public TicketDatabase(MongoHandler mongoHandler) {
        this.mongoHandler = mongoHandler;
    }

    public static void sendTicketNotification(Guild guild, TicketReason providedReason, long ticketId) {

        TextChannel notificationChannel = guild.getTextChannelById("1171043429610958888");

        assert notificationChannel != null;
        notificationChannel.sendMessage("<@&1171043690148540446>")
                .addEmbeds(new EmbedBuilder()
                        .setAuthor("Tickets")
                        .setColor(Color.ORANGE)
                        .setDescription("Ein Teammitglied ist bei dem Ticket gefragt.")
                        .addField("Grund", providedReason.getReason(), true)
                        .addField("Id", String.valueOf(ticketId), true)
                        .setTimestamp(Instant.now())
                        .build()).addActionRow(Button.primary("ticket-claim", "Ticket bearbeiten")).queue();


    }

    public void addToTicketCache(Member member, TicketReason reason) {
        this.ticketCache.put(member, reason);
    }

    public void removeFromTicketCache(Member member) {
        this.ticketCache.remove(member);
    }

    public Optional<Ticket> getTicketWithId(long ticketId) {

        if (!this.isTicketPersist(ticketId))
            return Optional.empty();


        Document document = this.collection().find(Filters.eq("ticketId", ticketId)).first();

        if (document == null)
            return Optional.empty();

        return Optional.of(Ticket.fromJson(document.toJson()));
    }

    private boolean isTicketPersist(long ticketId) {
        return this.collection().countDocuments(Filters.eq("ticketId", ticketId)) > 0L;
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

    public void updateTicket(Ticket ticket) {

        if (!this.isTicketPersist(ticket.ticketId())) {
            this.insertTicket(ticket);
            return;
        }

        this.collection().deleteOne(Filters.eq("ticketId", ticket.ticketId()));
        this.insertTicket(ticket);

    }

    public BsonObjectId insertTicket(Ticket ticket) {
        return Objects.requireNonNull(this.collection().insertOne(ticket.toDocument()).getInsertedId()).asObjectId();
    }

}

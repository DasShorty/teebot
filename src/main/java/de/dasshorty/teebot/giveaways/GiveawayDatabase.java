package de.dasshorty.teebot.giveaways;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GiveawayDatabase {
    private final MongoHandler mongoHandler;

    public GiveawayDatabase(MongoHandler mongoHandler) {
        this.mongoHandler = mongoHandler;
    }

    private MongoCollection<Document> collection() {
        return this.mongoHandler.collection("giveaways");
    }

    public void insertGiveaway(GiveawayDto giveawayDto) {
        this.collection().insertOne(giveawayDto.toDocument());
    }

    public List<GiveawayDto> getActiveGiveaways() {

        FindIterable<Document> documents = this.collection().find(Filters.eq("active", true));

        if (!documents.cursor().hasNext())
            return List.of();

        List<GiveawayDto> giveawayDtos = new ArrayList<>();

        documents.forEach(document -> {

            giveawayDtos.add(GiveawayDto.fromJson(document.toJson()));

        });

        return giveawayDtos;
    }

    public long countGiveaways() {
        return this.collection().countDocuments();
    }

    private boolean isGiveawayPersist(long giveawayId) {
        return this.collection().countDocuments(Filters.eq("giveawayId", giveawayId)) > 0L;
    }

    public Optional<GiveawayDto> getGiveaway(long giveawayId) {

        if (!this.isGiveawayPersist(giveawayId))
            return Optional.empty();

        Document document = this.collection().find(Filters.eq("giveawayId", giveawayId)).first();

        if (document == null || document.isEmpty())
            return Optional.empty();

        return Optional.of(GiveawayDto.fromJson(document.toJson()));
    }

    private void deleteGiveaway(long giveawayId) {

        if (!this.isGiveawayPersist(giveawayId))
            return;

        this.collection().deleteOne(Filters.eq("giveawayId", giveawayId));

    }

    public void updateGiveaway(GiveawayDto giveawayDto) {

        if (!this.isGiveawayPersist(giveawayDto.giveawayId())) {
            this.insertGiveaway(giveawayDto);
            return;
        }

        this.collection().deleteOne(Filters.eq("giveawayId", giveawayDto.giveawayId()));
        this.insertGiveaway(giveawayDto);

    }

}

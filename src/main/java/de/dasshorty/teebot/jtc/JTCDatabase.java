package de.dasshorty.teebot.jtc;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import org.bson.Document;

import java.util.Optional;

public class JTCDatabase {
    private static final Gson GSON = new Gson();
    private final MongoHandler mongoHandler;

    public JTCDatabase(MongoHandler mongoHandler) {
        this.mongoHandler = mongoHandler;
    }

    Optional<JTC> getJTC(String channelId) {

        Document document = this.collection().find(Filters.eq("channelId", channelId)).first();

        if (document == null) {
            return Optional.empty();
        }

        return Optional.of(GSON.fromJson(document.toJson(), JTC.class));
    }

    private MongoCollection<Document> collection() {
        return this.mongoHandler.collection("jtc");
    }

    void addJTC(JTC jtc) {
        this.collection().insertOne(GSON.fromJson(GSON.toJson(jtc), Document.class));
    }

    void removeJTC(String channelId) {
        this.collection().deleteOne(Filters.eq("channelId", channelId));
    }

    boolean isJTCExisting(String channelId) {
        Document document = this.collection().find(Filters.eq("channelId", channelId)).first();

        return document == null;
    }


}

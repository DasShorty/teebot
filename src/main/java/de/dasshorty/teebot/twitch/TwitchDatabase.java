package de.dasshorty.teebot.twitch;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class TwitchDatabase {

    private static final Gson GSON = new Gson();
    private final MongoHandler mongoHandler;

    public TwitchDatabase(MongoHandler mongoHandler) {
        this.mongoHandler = mongoHandler;
    }

    private MongoCollection<Document> collection() {
        return this.mongoHandler.collection("twitch");
    }

    boolean isChannelRegistered(String twitchChannel) {
        return this.collection().find(Filters.eq("twitchChannel", twitchChannel)).first() != null;
    }

    void addChannel(TwitchChannel channel) {

        if (this.isChannelRegistered(channel.twitchChannel()))
            return;


        this.collection().insertOne(GSON.fromJson(GSON.toJson(channel), Document.class));
    }

    void removeChannel(String twitchChannel) {

        if (!this.isChannelRegistered(twitchChannel))
            return;

        this.collection().deleteOne(Filters.eq("twitchChannel", twitchChannel));

    }

    List<TwitchChannel> getAllTwitchChannels() {

        List<TwitchChannel> list = new ArrayList<>();

        MongoCursor<Document> cursor = this.collection().find().cursor();

        while (cursor.hasNext()) {

            Document document = cursor.next();
            list.add(GSON.fromJson(document.toJson(), TwitchChannel.class));

        }

        return list;
    }
}

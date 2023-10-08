package de.dasshorty.teebot.twitch;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class TwitchDatabase {

    private static final Gson GSON = new Gson();
    private final MongoHandler mongoHandler;

    private MongoCollection<Document> collection() {
        return this.mongoHandler.collection("twitch");
    }

    public boolean isChannelRegistered(String twitchChannel) {
        return this.collection().find(Filters.eq("twitchChannel", twitchChannel)).first() != null;
    }

    public void addChannel(TwitchChannel channel) {

        if (this.isChannelRegistered(channel.twitchChannel()))
            return;


        this.collection().insertOne(GSON.fromJson(GSON.toJson(channel), Document.class));
    }

    public void removeChannel(String twitchChannel) {

        if (!this.isChannelRegistered(twitchChannel))
            return;

        this.collection().deleteOne(Filters.eq("twitchChannel", twitchChannel));

    }

    public List<TwitchChannel> getAllTwitchChannels() {

        val list = new ArrayList<TwitchChannel>();

        val cursor = this.collection().find().cursor();

        while (cursor.hasNext()) {
            list.add(GSON.fromJson(cursor.next().toJson(), TwitchChannel.class));
        }

        return list;
    }
}

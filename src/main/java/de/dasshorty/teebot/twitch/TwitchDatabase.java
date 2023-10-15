package de.dasshorty.teebot.twitch;

import com.google.gson.Gson;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

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

        List<TwitchChannel> list = new ArrayList<>();

       this.collection().find().subscribe(new Subscriber<>() {
           @Override
           public void onSubscribe(Subscription s) {
               s.request(1L);
           }

           @Override
           public void onNext(Document document) {
               list.add(GSON.fromJson(document.toJson(), TwitchChannel.class));
           }

           @Override
           public void onError(Throwable t) {
               t.fillInStackTrace();
           }

           @Override
           public void onComplete() {

           }
       });

        return list;
    }
}

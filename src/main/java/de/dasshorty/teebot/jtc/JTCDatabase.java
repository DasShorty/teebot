package de.dasshorty.teebot.jtc;

import com.google.gson.Gson;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class JTCDatabase {
    private static final Gson GSON = new Gson();
    private final MongoHandler mongoHandler;

    public JTCDatabase(MongoHandler mongoHandler) {
        this.mongoHandler = mongoHandler;
    }

    private MongoCollection<Document> collection() {
        return this.mongoHandler.collection("jtc");
    }

    public void addJTC(JTC jtc) {
        this.collection().insertOne(GSON.fromJson(GSON.toJson(jtc), Document.class));
    }

    public void removeJTC(String channelId) {
        this.collection().deleteOne(Filters.eq("channelId", channelId));
    }

    public CompletableFuture<Optional<JTC>> getJTC(String channelId) {

        CompletableFuture<Optional<JTC>> future = new CompletableFuture<>();

        this.collection().find(Filters.eq("channelId", channelId)).first().subscribe(new Subscriber<Document>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1L);
            }

            @Override
            public void onNext(Document document) {
                if (document == null) {
                    future.complete(Optional.empty());
                    return;
                }

                future.complete(Optional.of(GSON.fromJson(document.toJson(), JTC.class)));
            }

            @Override
            public void onError(Throwable t) {
                t.fillInStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });

        return future;
    }

    public boolean isJTCExisting(String channelId) {
        return this.collection().find(Filters.eq("channelId", channelId)).first() != null;
    }


}

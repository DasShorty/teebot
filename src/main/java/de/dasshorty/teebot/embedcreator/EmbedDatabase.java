package de.dasshorty.teebot.embedcreator;

import com.google.gson.Gson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.reactivestreams.client.MongoCollection;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EmbedDatabase {
    private static final String COLLECTION_NAME = "embeds";
    private static final Gson GSON = new Gson();
    private final Map<String, String> memberEmbedMap = new HashMap<>();
    private final MongoHandler mongoHandler;

    public EmbedDatabase(MongoHandler mongoHandler) {
        this.mongoHandler = mongoHandler;
    }

    public Map<String, String> getMemberEmbedMap() {
        return this.memberEmbedMap;
    }

    private void insertEmbed(@NotNull Embed embed) {

        this.getCollection().insertOne(GSON.fromJson(embed.toGson(), Document.class));
    }

    private void updateEmbed(@NotNull Embed embed) {

        List<Bson> list = List.of(
                Updates.set("author", embed.getAuthor()),
                Updates.set("content", embed.getContent()),
                Updates.set("fields", embed.getFields()),
                Updates.set("footer", embed.getFooter()),
                Updates.set("style", embed.getStyle())
        );

        this.getCollection().updateOne(Filters.eq("embedId", embed.getEmbedId()), list);
    }

    List<Embed> embeds() {

        List<Embed> list = new ArrayList<>();
        this.getCollection().find().subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Document document) {

                if (!document.isEmpty()) {
                    Embed embed = GSON.fromJson(document.toJson(), Embed.class);
                    list.add(embed);
                }

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

    public void deleteEmbed(String embedId) {

        this.getCollection().deleteOne(Filters.eq("embedId", embedId));

    }

    public void storeEmbed(Embed embed) {
        try {

            if (this.isEmbedEmpty(embed.getEmbedId()).get().booleanValue())
                this.insertEmbed(embed);
            else
                this.updateEmbed(embed);

        } catch (InterruptedException | ExecutionException exception) {
            exception.fillInStackTrace();
        }
    }

    private CompletableFuture<Boolean> isEmbedEmpty(String embedId) {

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        this.getCollection().find(Filters.eq("embedId", embedId)).first().subscribe(new Subscriber<Document>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1L);
            }

            @Override
            public void onNext(Document document) {
                future.complete(document != null);
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

    public CompletableFuture<Optional<Embed>> getEmbed(String embedId) {

        CompletableFuture<Optional<Embed>> future = new CompletableFuture<>();

        try {
            if (this.isEmbedEmpty(embedId).get().booleanValue()) {

                future.complete(Optional.empty());
                return future;

            }
        } catch (InterruptedException | ExecutionException exception) {
            exception.fillInStackTrace();
        }

        this.getCollection().find(Filters.eq("embedId", embedId)).first().subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1L);
            }

            @Override
            public void onNext(Document document) {

                if (document == null || document.isEmpty()) {
                    future.complete(Optional.empty());
                    return;
                }

                future.complete(Optional.of(GSON.fromJson(document.toJson(), Embed.class)))

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });

        return future;
    }

    private MongoCollection<Document> getCollection() {
        return this.mongoHandler.collection(COLLECTION_NAME);
    }


}

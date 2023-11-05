package de.dasshorty.teebot.embedcreator;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class EmbedDatabase {
    private static final Gson GSON = new Gson();
    private final Map<String, String> memberEmbedMap = new HashMap<>();
    private final MongoHandler mongoHandler;

    public EmbedDatabase(MongoHandler mongoHandler) {
        this.mongoHandler = mongoHandler;
    }

    public Map<String, String> getMemberEmbedMap() {
        return this.memberEmbedMap;
    }

    private void insertEmbed(Embed embed) {

        this.getCollection().insertOne(GSON.fromJson(embed.toGson(), Document.class));
    }

    private void updateEmbed(Embed embed) {

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


        MongoCursor<Document> cursor = this.getCollection().find().cursor();

        List<Embed> list = new ArrayList<>();

        while (cursor.hasNext()) {

            Document document = cursor.next();

            if (document.isEmpty())
                continue;

            Embed embed = GSON.fromJson(document.toJson(), Embed.class);

            list.add(embed);
        }

        return list;
    }

    void deleteEmbed(String embedId) {

        this.getCollection().deleteOne(Filters.eq("embedId", embedId));

    }

    private MongoCollection<Document> getCollection() {
        return this.mongoHandler.collection("embeds");
    }

    public void storeEmbed(Embed embed) {

        if (this.isEmbedEmpty(embed.getEmbedId()))
            this.insertEmbed(embed);
        else
            this.updateEmbed(embed);
    }

    private boolean isEmbedEmpty(String embedId) {
        return null == this.getCollection().find(Filters.eq("embedId", embedId)).first();
    }

    public CompletableFuture<Optional<Embed>> getEmbed(String embedId) {

        CompletableFuture<Optional<Embed>> future = new CompletableFuture<>();

        if (this.isEmbedEmpty(embedId)) {

            future.complete(Optional.empty());
            return future;

        }

        Document document = this.getCollection().find(Filters.eq("embedId", embedId)).first();

        if (document == null || document.isEmpty()) {
            future.complete(Optional.empty());
            return future;
        }

        future.complete(Optional.of(GSON.fromJson(document.toJson(), Embed.class)));


        return future;
    }


}

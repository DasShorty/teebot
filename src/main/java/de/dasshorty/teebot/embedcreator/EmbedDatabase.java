package de.dasshorty.teebot.embedcreator;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.Document;

import java.util.*;

@RequiredArgsConstructor
public class EmbedDatabase {

    private static final String COLLECTION_NAME = "embeds";
    private static final Gson GSON = new Gson();
    @Getter
    private final Map<String, String> memberEmbedMap = new HashMap<>();
    private final MongoHandler mongoHandler;

    private void insertEmbed(final Embed embed) {
        this.getCollection().insertOne(GSON.fromJson(embed.toGson(), Document.class));
    }

    private void updateEmbed(final Embed embed) {

        val list = List.of(
                Updates.set("author", embed.getAuthor()),
                Updates.set("content", embed.getContent()),
                Updates.set("fields", embed.getFields()),
                Updates.set("footer", embed.getFooter()),
                Updates.set("style", embed.getStyle())
        );

        this.getCollection().updateOne(Filters.eq("embedId", embed.getEmbedId()), list);
    }

    List<Embed> embeds() {

        val cursor = this.getCollection().find().cursor();

        val list = new ArrayList<Embed>();

        while (cursor.hasNext()) {

            val document = cursor.next();

            if (document.isEmpty())
                continue;

            val embed = GSON.fromJson(document.toJson(), Embed.class);

            list.add(embed);
        }

        return list;
    }

    public void deleteEmbed(final String embedId) {

        this.getCollection().deleteOne(Filters.eq("embedId", embedId));

    }

    public void storeEmbed(final Embed embed) {
        if (this.isEmbedEmpty(embed.getEmbedId()))
            this.insertEmbed(embed);
        else
            this.updateEmbed(embed);

    }

    private boolean isEmbedEmpty(String embedId) {
        return null == this.getCollection().find(Filters.eq("embedId", embedId)).first();
    }

    public Optional<Embed> getEmbed(String embedId) {

        if (this.isEmbedEmpty(embedId))
            return Optional.empty();

        val embedDocument = this.getCollection().find(Filters.eq("embedId", embedId)).first();

        if (null == embedDocument || embedDocument.isEmpty())
            return Optional.empty();

        return Optional.of(GSON.fromJson(embedDocument.toJson(), Embed.class));
    }

    private MongoCollection<Document> getCollection() {
        return this.mongoHandler.collection(COLLECTION_NAME);
    }


}

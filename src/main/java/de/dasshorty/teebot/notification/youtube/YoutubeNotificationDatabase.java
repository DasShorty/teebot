package de.dasshorty.teebot.notification.youtube;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import org.bson.Document;

import java.util.Objects;

public record YoutubeNotificationDatabase(MongoHandler mongoHandler) {

    private MongoCollection<Document> collection() {
        return this.mongoHandler.collection("youtubeNotify");
    }

    void setLastVideoId(String id) {

        if (this.collection().countDocuments() != 0L) {

            Document first = this.collection().find().first();
            assert first != null;
            String youtubeNotify = first.getString("youtubeNotify");
            this.collection().updateOne(Filters.eq("youtubeNotify", youtubeNotify), Updates.set("youtubeNotify", youtubeNotify));

            return;
        }

        Document document = new Document();
        document.put("youtubeNotify", id);
        this.collection().insertOne(document);
    }

    boolean compareIds(String id) {

        Document first = this.collection().find().first();

        if (first == null)
            return false;

        return Objects.equals(first.getString("youtubeNotify"), id);
    }
}

package de.dasshorty.teebot.api.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoHandler {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoHandler() {
        this.mongoClient = MongoClients.create(System.getenv("MONGO_URL"));
        this.database = this.mongoClient.getDatabase("teebot");
    }

    public MongoCollection<Document> collection(String name) {

        try {
            return this.database.getCollection(name);
        } catch (IllegalArgumentException exception) {
            this.database.createCollection(name);
        }


        return this.database.getCollection(name);
    }
}

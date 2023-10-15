package de.dasshorty.teebot.selfroles;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SelfRoleDatabase {

    private static final Gson GSON = new Gson();
    private final MongoHandler mongoHandler;

    public SelfRoleDatabase(MongoHandler mongoHandler) {
        this.mongoHandler = mongoHandler;
    }

    private MongoCollection<Document> collection() {
        return this.mongoHandler.collection("self-roles");
    }

    boolean isSelfRolePersist(String roleId) {
        return this.collection().find(Filters.eq("id", roleId)).first() != null;
    }

    boolean addSelfRole(SelfRole selfRole) {

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (this.isSelfRolePersist(selfRole.id())) {
            return false;
        }

        return this.collection().insertOne(GSON.fromJson(selfRole.toGson(), Document.class)).wasAcknowledged()
    }

    void removeSelfRole(String roleId) {
        this.collection().deleteOne(Filters.eq("id", roleId));
    }

    List<SelfRole> getAllSelfRolesByCategory(SelfRoleCategory category) {

        List<SelfRole> list = new ArrayList<>();

        MongoCursor<Document> cursor = this.collection().find(Filters.eq("category", category.name())).cursor();

        while (cursor.hasNext()) {

            Document document = cursor.next();
            list.add(GSON.fromJson(document.toJson(), SelfRole.class));

        }

        return list;
    }


}

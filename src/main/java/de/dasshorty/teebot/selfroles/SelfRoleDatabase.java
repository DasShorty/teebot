package de.dasshorty.teebot.selfroles;

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
public class SelfRoleDatabase {

    private static final Gson GSON = new Gson();
    private final MongoHandler mongoHandler;

    private MongoCollection<Document> collection() {
        return this.mongoHandler.collection("self-roles");
    }

    boolean isSelfRolePersist(String roleId) {
        return this.collection().find(Filters.eq("id", roleId)).first() != null;
    }

    boolean addSelfRole(SelfRole selfRole) {

        if (this.isSelfRolePersist(selfRole.id()))
            return false;

        return this.collection().insertOne(GSON.fromJson(selfRole.toGson(), Document.class)).wasAcknowledged();
    }

    void removeSelfRole(String roleId) {
        this.collection().deleteOne(Filters.eq("id", roleId));
    }

    List<SelfRole> getAllSelfRolesByCategory(SelfRoleCategory category) {

        val list = new ArrayList<SelfRole>();

        val cursor = this.collection().find(Filters.eq("category", category.name())).cursor();

        while (cursor.hasNext()) {
            list.add(GSON.fromJson(cursor.next().toJson(), SelfRole.class));
        }

        return list;
    }


}

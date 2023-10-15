package de.dasshorty.teebot.selfroles;

import com.google.gson.Gson;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.Success;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import org.bson.Document;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

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

    CompletableFuture<Boolean> addSelfRole(SelfRole selfRole) {

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        if (this.isSelfRolePersist(selfRole.id())) {
            future.complete(false);
            return future;
        }

        this.collection().insertOne(GSON.fromJson(selfRole.toGson(), Document.class)).subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1L);
            }

            @Override
            public void onNext(Success success) {
                future.complete(success == Success.SUCCESS);
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

    void removeSelfRole(String roleId) {
        this.collection().deleteOne(Filters.eq("id", roleId));
    }

    List<SelfRole> getAllSelfRolesByCategory(SelfRoleCategory category) {

        List<SelfRole> list = new ArrayList<>();

        this.collection().find(Filters.eq("category", category.name())).subscribe(new Subscriber<Document>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1L);
            }

            @Override
            public void onNext(Document document) {
                list.add(GSON.fromJson(document.toJson(), SelfRole.class));
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

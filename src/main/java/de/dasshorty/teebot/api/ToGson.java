package de.dasshorty.teebot.api;

import com.google.gson.Gson;

public interface ToGson {

    default String toGson() {
        return new Gson().toJson(this);
    }

}

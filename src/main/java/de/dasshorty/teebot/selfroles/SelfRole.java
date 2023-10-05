package de.dasshorty.teebot.selfroles;

import de.dasshorty.teebot.api.ToGson;

record SelfRole(String id, SelfRoleCategory category, String name) implements ToGson {
}

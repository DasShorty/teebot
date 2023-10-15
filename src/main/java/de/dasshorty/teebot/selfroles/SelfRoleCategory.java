package de.dasshorty.teebot.selfroles;

import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public enum SelfRoleCategory {

    COLOR("Farben"),
    INTERESTS("Interessen"),
    GENDER("Geschlecht"),
    PRONOUN("Pronomen"),
    SEXUALITY("Sexualität");

    private final String displayName;

    SelfRoleCategory(String displayName) {
        this.displayName = displayName;
    }

    @NotNull
    static StringSelectMenu buildMenu(SelfRoleCategory category, @NotNull SelfRoleDatabase database, boolean multiAction) {

        StringSelectMenu.Builder builder = StringSelectMenu.create("self-roles");

        List<SelfRole> allRolesFromCategory = database.getAllSelfRolesByCategory(category);

        allRolesFromCategory.forEach(selfRole -> builder.addOption(selfRole.name(), selfRole.id()));

        if (multiAction)
            builder.setRequiredRange(1, 25);

        return builder.build();
    }

    public String getDisplayName() {
        return this.displayName;
    }

}

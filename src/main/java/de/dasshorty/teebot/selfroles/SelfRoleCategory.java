package de.dasshorty.teebot.selfroles;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum SelfRoleCategory {

    COLOR("Farben"),
    INTERESTS("Interessen"),
    GENDER("Geschlecht"),
    PRONOUN("Pronomen"),
    SEXUALITY("Sexualität");
    private final String displayName;

    @NotNull
    static StringSelectMenu buildMenu(SelfRoleCategory category, @NotNull SelfRoleDatabase database, boolean multiAction) {

        val builder = StringSelectMenu.create("self-roles");

        val allRolesFromCategory = database.getAllSelfRolesByCategory(category);

        allRolesFromCategory.forEach(selfRole -> builder.addOption(selfRole.name(), selfRole.id()));

        if (multiAction)
            builder.setRequiredRange(1, 25);

        return builder.build();
    }

}

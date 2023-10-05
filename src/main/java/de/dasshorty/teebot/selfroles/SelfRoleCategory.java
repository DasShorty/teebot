package de.dasshorty.teebot.selfroles;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum SelfRoleCategory {

    COLOR("Farben", "Wähle deine Farben."),
    INTERESTS("Interessen", "Wähle deine Interessen aus."),
    GENDER("Geschlecht", "Wähle dein Geschlecht aus."),
    PRONOUN("Pronomen", "Wähle deine Pronomen aus."),
    SEXUALITY("Sexualität", "Wähle deine Sexualität aus.");
    private final String displayName;
    private final String menuText;

    static StringSelectMenu buildMenu(SelfRoleCategory category, SelfRoleDatabase database, boolean multiAction) {

        val builder = StringSelectMenu.create("self-roles-" + category.name().toLowerCase(Locale.ROOT));

        val allRolesFromCategory = database.getAllSelfRolesByCategory(category);

        allRolesFromCategory.forEach(selfRole -> builder.addOption(selfRole.name(), selfRole.id()));

        if (multiAction)
            builder.setRequiredRange(1, 100);

        return builder.build();
    }

}

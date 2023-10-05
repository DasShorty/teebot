package de.dasshorty.teebot.api.menu;

import de.dasshorty.teebot.api.menu.entity.EntitySelectionMenu;
import de.dasshorty.teebot.api.menu.string.StringSelectionMenu;
import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SelectionMenuHandler extends ListenerAdapter {

    private final List<EntitySelectionMenu> entitySelectionMenus = new ArrayList<>();
    private final List<StringSelectionMenu> stringSelectionMenus = new ArrayList<>();

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        stringSelectionMenus.forEach(stringSelectionMenu -> {
            if (stringSelectionMenu.id().equals(event.getSelectMenu().getId()))
                stringSelectionMenu.onExecute(event);
        });
    }

    @Override
    public void onEntitySelectInteraction(@NotNull EntitySelectInteractionEvent event) {
        entitySelectionMenus.forEach(entitySelectionMenus -> {
            if (entitySelectionMenus.id().equals(event.getSelectMenu().getId()))
                entitySelectionMenus.onExecute(event);
        });
    }
}

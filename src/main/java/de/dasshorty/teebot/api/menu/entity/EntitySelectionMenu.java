package de.dasshorty.teebot.api.menu.entity;

import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;

public interface EntitySelectionMenu {

    String id();

    void onExecute(EntitySelectInteractionEvent event);

}

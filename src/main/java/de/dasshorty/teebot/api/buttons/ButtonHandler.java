package de.dasshorty.teebot.api.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ButtonHandler extends ListenerAdapter {
    private final List<Button> buttons = new ArrayList<>();

    public List<Button> getButtons() {
        return this.buttons;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        buttons.forEach(button -> {
            if (button.id().equals(event.getButton().getId())) button.onExecute(event);
        });

    }
}

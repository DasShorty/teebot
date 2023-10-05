package de.dasshorty.teebot.api.modal;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ModalHandler extends ListenerAdapter {

    private final List<Modal> modals = new ArrayList<>();

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        modals.forEach(modal -> {
            if (modal.id().equals(event.getInteraction().getModalId())) modal.onExecute(event);
        });

    }
}

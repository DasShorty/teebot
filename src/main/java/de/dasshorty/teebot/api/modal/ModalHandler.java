package de.dasshorty.teebot.api.modal;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModalHandler extends ListenerAdapter {
    private final List<Modal> modals = new ArrayList<>();

    public List<Modal> getModals() {
        return this.modals;
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        modals.forEach(modal -> {
            if (modal.id().equals(event.getInteraction().getModalId())) modal.onExecute(event);
        });

    }
}

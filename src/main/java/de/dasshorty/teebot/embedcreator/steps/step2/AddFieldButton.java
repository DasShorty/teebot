package de.dasshorty.teebot.embedcreator.steps.step2;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import lombok.val;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;


public class AddFieldButton implements Button {

    @Override
    public String id() {
        return "embed-creator-add-field";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        val member = event.getMember();
        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN) || Roles.hasMemberRole(member, Roles.DEVELOPER))) {
            event.reply("Du hast keine Rechte für diese Aktion!")
                    .setEphemeral(true).queue();
            return;
        }

        val modal = Modal.create("embed-creator-step2", "Feld hinzufügen")
                .addActionRow(TextInput.create("title", "Titel", TextInputStyle.SHORT)
                        .setRequired(true)
                        .setPlaceholder("Setze den Titel des Feldes")
                        .build())
                .addActionRow(TextInput.create("description", "Beschreibung", TextInputStyle.PARAGRAPH)
                        .setPlaceholder("Setze die Beschreibung des Feldes")
                        .setRequired(true)
                        .build()).build();

        event.replyModal(modal).queue();

    }
}

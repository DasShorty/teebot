package de.dasshorty.teebot.embedcreator.steps.step4.color;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import lombok.val;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

public class AddColorButton implements Button {
    @Override
    public String id() {
        return "embed-creator-add-color";
    }

    @Override
    public void onExecute(@NotNull ButtonInteractionEvent event) {
        val member = event.getMember();
        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN) || Roles.hasMemberRole(member, Roles.DEVELOPER))) {
            event.reply("Du hast keine Rechte für diese Aktion!")
                    .setEphemeral(true).queue();
            return;
        }

        val modal = Modal.create("embed-creator-add-color", "Farbe hinzufügen")
                .addActionRow(TextInput.create("color", "Farbe", TextInputStyle.SHORT)
                        .setRequired(true)
                        .setPlaceholder("Farbe als HEX Format z.B. #FFFFFF")
                        .build()).build();

        event.replyModal(modal).queue();
    }
}

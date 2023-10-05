package de.dasshorty.teebot.embedcreator.steps.step1;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@RequiredArgsConstructor
public class AuthorButton implements Button {

    private final EmbedDatabase embedDatabase;

    @Override
    public String id() {
        return "embed-creator-step1";
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

        val modal = Modal.create("embed-builder-autor", "Setze den Autor")
                .addActionRow(TextInput.create("author", "Embed Author", TextInputStyle.SHORT)
                        .setPlaceholder("Setze den Author des Embeds")
                        .setRequired(true)
                        .build())
                .addActionRow(TextInput.create("image", "Bild", TextInputStyle.SHORT)
                        .setPlaceholder("Setze, als URL, ein Bild")
                        .setRequired(true)
                        .build())
                .addActionRow(TextInput.create("url", "URL", TextInputStyle.SHORT)
                        .setPlaceholder("Setze eine URL die als Link funktioniert")
                        .setRequired(true)
                        .build())
                .build();

        event.replyModal(modal).queue();
    }
}

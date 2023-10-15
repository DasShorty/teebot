package de.dasshorty.teebot.embedcreator.steps.step3;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class AddFooterButton implements Button {
    @Override
    public String id() {
        return "embed-creator-add-footer";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        Member member = event.getMember();
        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN) || Roles.hasMemberRole(member, Roles.DEVELOPER))) {
            event.reply("Du hast keine Rechte für diese Aktion!")
                    .setEphemeral(true).queue();
            return;
        }

        Modal modal = Modal.create("embed-creator-step3", "Footer")
                .addActionRow(TextInput.create("footer", "Footer", TextInputStyle.SHORT)
                        .setRequired(true)
                        .setPlaceholder("Setze den Footer")
                        .build()).build();

        event.replyModal(modal).queue();
    }
}

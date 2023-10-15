package de.dasshorty.teebot.embedcreator.steps.step5;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class FinishEmbedButton implements Button {

    private final EmbedDatabase embedDatabase;

    public FinishEmbedButton(EmbedDatabase embedDatabase) {
        this.embedDatabase = embedDatabase;
    }

    @Override
    public String id() {
        return "embed-creator-step5";
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

        String embedId = this.embedDatabase.getMemberEmbedMap().get(member.getId());

        event.reply("Das Embed wurde erfolgreich in der Datenbank gespeichert! Und kann via " + embedId + " aufgerufen werden!").setEphemeral(true).queue();

        this.embedDatabase.getMemberEmbedMap().remove(member.getId());
    }
}

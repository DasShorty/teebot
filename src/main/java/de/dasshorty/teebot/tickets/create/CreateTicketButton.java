package de.dasshorty.teebot.tickets.create;

import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.tickets.TicketReason;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class CreateTicketButton implements Button {

    @Override
    public String id() {
        return "ticket-create";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        StringSelectMenu.Builder builder = StringSelectMenu.create("ticket-create");

        builder.setRequiredRange(1, 1);

        for (TicketReason value : TicketReason.values()) {
            builder.addOption(value.getReason(), value.name());
        }

        event.reply("Wähle **eine** Kategorie deines Anliegens aus.").setEphemeral(true).addActionRow(builder.build()).queue();
    }
}

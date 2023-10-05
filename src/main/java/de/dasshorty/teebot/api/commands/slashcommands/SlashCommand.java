package de.dasshorty.teebot.api.commands.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public interface SlashCommand {

    CommandDataImpl commandData();
    void onExecute(SlashCommandInteractionEvent event);
}

package de.dasshorty.teebot.api.commands.usercommands;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public interface UserCommand {

    CommandDataImpl commandData();

    void onExecute(UserContextInteractionEvent event);

}

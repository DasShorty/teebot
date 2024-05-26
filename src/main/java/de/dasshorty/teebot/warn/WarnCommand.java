package de.dasshorty.teebot.warn;

import de.dasshorty.teebot.api.commands.usercommands.UserCommand;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class WarnCommand implements UserCommand {
    @Override
    public CommandDataImpl commandData() {
        return new CommandDataImpl(Command.Type.USER, "warn");
    }

    @Override
    public void onExecute(UserContextInteractionEvent event) {

    }
}

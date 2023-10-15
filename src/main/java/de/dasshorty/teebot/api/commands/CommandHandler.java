package de.dasshorty.teebot.api.commands;

import de.dasshorty.teebot.api.commands.messagecommands.MessageCommand;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import de.dasshorty.teebot.api.commands.usercommands.UserCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandHandler extends ListenerAdapter {
    private final List<UserCommand> userCommands = new ArrayList<>();
    private final List<SlashCommand> slashCommands = new ArrayList<>();
    private final List<MessageCommand> messageCommands = new ArrayList<>();

    public List<UserCommand> getUserCommands() {
        return this.userCommands;
    }

    public List<SlashCommand> getSlashCommands() {
        return this.slashCommands;
    }

    public List<MessageCommand> getMessageCommands() {
        return this.messageCommands;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        this.slashCommands.forEach(slashCommand -> {
            if (slashCommand.commandData().getName().equals(event.getFullCommandName().split(" ")[0]))
                slashCommand.onExecute(event);
        });
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        this.userCommands.forEach(userCommand -> {
            if (userCommand.commandData().getName().equals(event.getFullCommandName()))
                userCommand.onExecute(event);
        });
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        this.messageCommands.forEach(messageCommand -> {
            if (messageCommand.commandData().getName().equals(event.getName()))
                messageCommand.onExecute(event);
        });
    }

    public void addSlashCommand(SlashCommand command) {
        this.slashCommands.add(command);
    }

    public void updateCommands(Guild guild) {
        CommandListUpdateAction action = guild.updateCommands();

        System.out.println("Updating commands...");

        this.slashCommands.forEach(slashCommand -> {
            System.out.println("Updating: " + slashCommand.commandData().getName());
            action.addCommands(slashCommand.commandData()).queueAfter(1, TimeUnit.SECONDS);
        });

        this.messageCommands.forEach(messageCommand -> {
            System.out.println("Updating: " + messageCommand.commandData().getName());
            action.addCommands(messageCommand.commandData()).queueAfter(1, TimeUnit.SECONDS);
        });
        this.userCommands.forEach(userCommand -> {
            System.out.println("Updating: " + userCommand.commandData().getName());
            action.addCommands(userCommand.commandData()).queueAfter(1, TimeUnit.SECONDS);
        });

        action.queue();
        System.out.println("Update finished!");
    }
}

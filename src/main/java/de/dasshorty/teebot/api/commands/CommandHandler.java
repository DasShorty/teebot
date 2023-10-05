package de.dasshorty.teebot.api.commands;

import de.dasshorty.teebot.api.commands.messagecommands.MessageCommand;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import de.dasshorty.teebot.api.commands.usercommands.UserCommand;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
public class CommandHandler extends ListenerAdapter {

    private final List<UserCommand> userCommands = new ArrayList<>();
    private final List<SlashCommand> slashCommands = new ArrayList<>();
    private final List<MessageCommand> messageCommands = new ArrayList<>();

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
        val commandListUpdateAction = guild.updateCommands();

        System.out.println("Updating commands...");

        this.slashCommands.forEach(slashCommand -> {
            System.out.println("Updating: " + slashCommand.commandData().getName());
            commandListUpdateAction.addCommands(slashCommand.commandData()).queueAfter(1, TimeUnit.SECONDS);
        });

        this.messageCommands.forEach(messageCommand -> {
            System.out.println("Updating: " + messageCommand.commandData().getName());
            commandListUpdateAction.addCommands(messageCommand.commandData()).queueAfter(1, TimeUnit.SECONDS);
        });
        this.userCommands.forEach(userCommand -> {
            System.out.println("Updating: " + userCommand.commandData().getName());
            commandListUpdateAction.addCommands(userCommand.commandData()).queueAfter(1, TimeUnit.SECONDS);
        });

        commandListUpdateAction.queue();
        System.out.println("Update finished!");
    }
}

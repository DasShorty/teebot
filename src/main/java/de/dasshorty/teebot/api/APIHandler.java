package de.dasshorty.teebot.api;


import de.dasshorty.teebot.api.buttons.Button;
import de.dasshorty.teebot.api.buttons.ButtonHandler;
import de.dasshorty.teebot.api.commands.CommandHandler;
import de.dasshorty.teebot.api.commands.messagecommands.MessageCommand;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import de.dasshorty.teebot.api.commands.usercommands.UserCommand;
import de.dasshorty.teebot.api.menu.SelectionMenuHandler;
import de.dasshorty.teebot.api.menu.entity.EntitySelectionMenu;
import de.dasshorty.teebot.api.menu.string.StringSelectionMenu;
import de.dasshorty.teebot.api.modal.Modal;
import de.dasshorty.teebot.api.modal.ModalHandler;
import lombok.Getter;
import net.dv8tion.jda.api.JDABuilder;

@Getter
public class APIHandler {

    private final ButtonHandler buttonHandler;
    private final CommandHandler commandHandler;
    private final SelectionMenuHandler selectionMenuHandler;
    private final ModalHandler modalHandler;
    public APIHandler(JDABuilder builder) {
        this.buttonHandler = new ButtonHandler();
        this.commandHandler = new CommandHandler();
        this.selectionMenuHandler = new SelectionMenuHandler();
        this.modalHandler = new ModalHandler();

        builder.addEventListeners(this.buttonHandler);
        builder.addEventListeners(this.commandHandler);
        builder.addEventListeners(this.selectionMenuHandler);
        builder.addEventListeners(this.modalHandler);
    }

    public void addSlashCommand(SlashCommand slashCommand) {
        this.commandHandler.addSlashCommand(slashCommand);
    }

    public void addMessageCommand(MessageCommand messageCommand) {
        this.commandHandler.getMessageCommands().add(messageCommand);
    }

    public void addUSerCommand(UserCommand userCommand) {
        this.commandHandler.getUserCommands().add(userCommand);
    }

    public void addEntityMenu(EntitySelectionMenu selectionMenu) {
        this.selectionMenuHandler.getEntitySelectionMenus().add(selectionMenu);
    }

    public void addStringMenu(StringSelectionMenu selectionMenu) {
        this.selectionMenuHandler.getStringSelectionMenus().add(selectionMenu);
    }

    public void addButton(Button button) {
        this.buttonHandler.getButtons().add(button);
    }

    public void addModal(Modal modal) {
        this.modalHandler.getModals().add(modal);
    }

}

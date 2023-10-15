package de.dasshorty.teebot.api.buttons;

import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * Class made by DasShorty ~Anthony
 */

public class ButtonDisabler {
    private final MessageChannelUnion channelUnion;
    private final String messageID;
    public ButtonDisabler(MessageChannelUnion channelUnion, String messageID) {
        this.channelUnion = channelUnion;
        this.messageID = messageID;
    }

    public void withButton(Button... button) {
        this.channelUnion.editMessageComponentsById(messageID, ActionRow.of(button)).queue();
    }


}

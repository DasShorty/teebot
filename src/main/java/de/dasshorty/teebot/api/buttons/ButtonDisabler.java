package de.dasshorty.teebot.api.buttons;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

/**
 * Class made by DasShorty ~Anthony
 */

@RequiredArgsConstructor
public class ButtonDisabler {

    private final MessageChannelUnion channelUnion;
    private final String messageID;

    public void withButton(Button... button) {
        this.channelUnion.editMessageComponentsById(messageID, ActionRow.of(button)).queue();
    }


}

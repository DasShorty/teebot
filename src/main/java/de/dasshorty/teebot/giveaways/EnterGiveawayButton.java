package de.dasshorty.teebot.giveaways;

import de.dasshorty.teebot.api.buttons.Button;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class EnterGiveawayButton implements Button {

    private final GiveawayDatabase giveawayDatabase;

    public EnterGiveawayButton(GiveawayDatabase giveawayDatabase) {
        this.giveawayDatabase = giveawayDatabase;
    }

    @Override
    public String id() {
        return "enter-giveaway";
    }

    @Override
    public void onExecute(ButtonInteractionEvent event) {

        event.deferReply(true).queue();

        Optional<String> optionalGiveawayId = this.getGiveawayIdFromMessage(event.getMessage());

        if (optionalGiveawayId.isEmpty()) {
            event.getHook().editOriginal("Etwas ist schiefgelaufen! Bitte kontaktiere den Support!").queue();
            return;
        }

        long giveawayId = Long.parseLong(optionalGiveawayId.get());

        Optional<GiveawayDto> optionalGiveaway = this.giveawayDatabase.getGiveaway(giveawayId);

        if (optionalGiveaway.isEmpty()) {
            event.getHook().editOriginal("Das Giveaway konnte nicht gefunden werden!").queue();
            return;
        }

        GiveawayDto giveawayDto = optionalGiveaway.get();

        String memberId = event.getMember().getId();

        if (giveawayDto.enteredMemberIds().contains(memberId)) {
            event.getHook().editOriginal("Du nimmst bereits am Giveaway teil!").queue();
            return;
        }

        giveawayDto.enteredMemberIds().add(memberId);

        this.giveawayDatabase.updateGiveaway(giveawayDto);

        event.getHook().editOriginal("Du nimmst nun an dem Giveaway teil!").queue();
    }

    private Optional<String> getGiveawayIdFromMessage(@NotNull Message message) {

        List<MessageEmbed> embeds = message.getEmbeds();

        if (embeds.isEmpty())
            return Optional.empty();

        MessageEmbed messageEmbed = embeds.get(0);

        List<MessageEmbed.Field> fields = messageEmbed.getFields();

        if (fields.size() != 3)
            return Optional.empty();

        MessageEmbed.Field field = fields.get(2);

        return Optional.ofNullable(field.getValue());
    }
}

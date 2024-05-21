package de.dasshorty.teebot.giveaways;

import de.dasshorty.teebot.api.buttons.Button;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class EnterGiveawayButton implements Button {

    private final GiveawayRepository giveawayRepo;

    public EnterGiveawayButton(GiveawayRepository giveawayRepo) {
        this.giveawayRepo = giveawayRepo;
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

        Optional<GiveawayDto> optional = this.giveawayRepo.findById(optionalGiveawayId.get());

        if (optional.isEmpty()) {
            event.getHook().editOriginal("Das Giveaway konnte nicht gefunden werden!").queue();
            return;
        }

        GiveawayDto giveawayDto = optional.get();

        String memberId = event.getMember().getId();

        if (giveawayDto.getEnteredMemberIds().contains(memberId)) {
            event.getHook().editOriginal("Du nimmst bereits am Giveaway teil!").queue();
            return;
        }

        giveawayDto.getEnteredMemberIds().add(memberId);

        this.giveawayRepo.save(giveawayDto);
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

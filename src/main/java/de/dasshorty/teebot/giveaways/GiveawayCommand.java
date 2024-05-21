package de.dasshorty.teebot.giveaways;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.bson.types.ObjectId;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

public class GiveawayCommand implements SlashCommand {

    private final GiveawayRepository giveawayRepo;
    private final GiveawayManager giveawayManager;

    public GiveawayCommand(GiveawayRepository giveawayRepo, GiveawayManager giveawayManager) {
        this.giveawayRepo = giveawayRepo;
        this.giveawayManager = giveawayManager;
    }

    @Override
    public CommandDataImpl commandData() {
        return new CommandDataImpl("giveaway", "Giveaways")
                .addSubcommands(
                        new SubcommandData("create", "Erstelle ein Giveaway")
                                .addOptions(
                                        new OptionData(OptionType.INTEGER, "winner", "Gewinner bei dem Giveaway", true),
                                        new OptionData(OptionType.INTEGER, "end", "Ende des Giveaways (in Sekunden)", true),
                                        new OptionData(OptionType.STRING, "description", "Beschreibung des Giveaways", true)
                                ),
                        new SubcommandData("start", "Starte ein Giveaway")
                                .addOption(OptionType.INTEGER, "id", "Die Giveaway ID", true)
                );
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {

        Member member = event.getMember();

        if (!Roles.hasMemberRole(member, Roles.DEVELOPER, Roles.ADMIN)) {
            event.reply("Du hast keine Rechte für diese Funktion!").setEphemeral(true).queue();
            return;
        }

        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "create" -> {

                event.deferReply(true).queue();

                int winnerCount = Objects.requireNonNull(event.<Integer>getOption("winner", OptionMapping::getAsInt)).intValue();
                long endTime = Objects.requireNonNull(event.<Long>getOption("end", OptionMapping::getAsLong)).longValue();
                String description = event.getOption("description", OptionMapping::getAsString);

                GiveawayDto dto = new GiveawayDto();
                dto.setGiveawayId(new ObjectId().toHexString());
                dto.setWinnerCount(winnerCount);
                dto.setDescription(description);
                dto.setEndTime(endTime);

                this.giveawayRepo.save(dto);

                event.getHook().editOriginal("Das Giveaway wurde als ID ( **" + dto.getGiveawayId() + "** ) abgespeichert! Um nun das Giveaway zu starten, **gebe** /giveaway start <id> ein!").queue();
            }

            case "start" -> {

                event.deferReply(true).queue();

                String giveawayId = Objects.requireNonNull(event.getOption("id", OptionMapping::getAsString));

                Optional<GiveawayDto> optionalGiveaway = this.giveawayRepo.findById(giveawayId);

                InteractionHook hook = event.getHook();

                if (optionalGiveaway.isEmpty()) {
                    hook.editOriginal("Das Giveaway konnte **mit** der angegebenen ID nicht gefunden werden!").queue();
                    return;
                }

                GiveawayDto dto = optionalGiveaway.get();

                TextChannel giveawayChannel = Objects.requireNonNull(event.getGuild()).getTextChannelById(System.getenv("GIVEAWAY_CHANNEL"));

                assert giveawayChannel != null;
                giveawayChannel.sendMessageEmbeds(new EmbedBuilder()
                                .setAuthor("Giveaways")
                                .setColor(Color.decode("#f7d31b"))
                                .setDescription(dto.getDescription())
                                .addField("Gewinner", String.valueOf(dto.getWinnerCount()), true)
                                .addField("Läuft bis", dto.getTimestamp(), true)
                                .build())
                        .addActionRow(Button.success("enter-giveaway", "Giveaway beitreten"))
                        .queue(message -> {

                            dto.setActive(true);
                            dto.setMessageId(message.getId());

                            this.giveawayRepo.save(dto);

                            hook.editOriginal("Das Giveaway (**" + dto.getGiveawayId() + "**) wurde gestartet!").queue();
                        });


            }
        }

    }
}

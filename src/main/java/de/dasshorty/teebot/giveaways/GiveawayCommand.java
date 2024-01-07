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

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class GiveawayCommand implements SlashCommand {

    private final GiveawayDatabase giveawayDatabase;
    private final GiveawayManager giveawayManager;

    public GiveawayCommand(GiveawayDatabase giveawayDatabase, GiveawayManager giveawayManager) {
        this.giveawayDatabase = giveawayDatabase;
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

                int winner = Objects.requireNonNull(event.<Integer>getOption("winner", OptionMapping::getAsInt)).intValue();
                long endTime = Objects.requireNonNull(event.<Long>getOption("end", OptionMapping::getAsLong)).longValue();
                String description = event.getOption("description", OptionMapping::getAsString);

                long giveawayId = this.giveawayDatabase.countGiveaways();

                Giveaway giveaway = new Giveaway(giveawayId, winner, description, endTime, null, false, new ArrayList<>());

                this.giveawayDatabase.insertGiveaway(giveaway);

                event.getHook().editOriginal("Das Giveaway wurde als ID ( *" + giveawayId + "* ) abgespeichert! Um nun das Giveaway zu starten, **gebe** /giveaway start <id> ein!").queue();
            }

            case "start" -> {

                event.deferReply(true).queue();

                long giveawayId = Objects.requireNonNull(event.getOption("id", OptionMapping::getAsLong)).longValue();

                Optional<Giveaway> optionalGiveaway = this.giveawayDatabase.getGiveaway(giveawayId);

                InteractionHook hook = event.getHook();

                if (optionalGiveaway.isEmpty()) {
                    hook.editOriginal("Das Giveaway konnte **mit** der angegebenen ID nicht gefunden werden!").queue();
                    return;
                }

                Giveaway giveaway = optionalGiveaway.get();


                TextChannel giveawayChannel = Objects.requireNonNull(event.getGuild()).getTextChannelById("1159846464990740642");

                assert giveawayChannel != null;
                giveawayChannel.sendMessageEmbeds(new EmbedBuilder()
                        .setAuthor("Giveaways")
                        .setColor(Color.decode("#f7d31b"))
                        .setDescription(giveaway.description())
                        .addField("Gewinner", String.valueOf(giveaway.winner()), true)
                        .addField("Läuft bis", "<t:" + giveaway.getEndTimeCalculated() + ":R>", true)
                        .addField("ID", String.valueOf(giveawayId), false)
                        .build())
                        .addActionRow(Button.success("enter-giveaway", "Giveaway beitreten"))
                        .queue(message -> {


                    Giveaway updatedGiveaway = new Giveaway(giveaway.giveawayId(), giveaway.winner(), giveaway.description(), giveaway.endTime(), message.getId(), true, new ArrayList<>());
                    this.giveawayDatabase.updateGiveaway(updatedGiveaway);

                    hook.editOriginal("Das Giveaway wurde gestartet!").queue();

                    this.giveawayManager.createScheduledTask(giveaway);

                });


            }
        }

    }
}

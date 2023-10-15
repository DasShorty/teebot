package de.dasshorty.teebot.twitch;

import de.dasshorty.teebot.api.Paginator;
import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class TwitchCommand implements SlashCommand {

    private final TwitchBot twitchBot;

    public TwitchCommand(TwitchBot twitchBot) {
        this.twitchBot = twitchBot;
    }

    @Override
    public CommandDataImpl commandData() {
        return new CommandDataImpl("twitch", "Twitch Bot verwalten")
                .addSubcommands(
                        new SubcommandData("add", "Füge einen Channel zu der Liste hinzu")
                                .addOption(OptionType.STRING, "twitch-name", "Der Twitch Channel der hinzugefügt werden soll.", true),
                        new SubcommandData("remove", "Entferne einen Channel aus der Liste")
                                .addOption(OptionType.STRING, "twitch-name", "Der Twitch Channel der entfernt werden soll.", true),
                        new SubcommandData("list", "Liste alle Twitch Channel auf")
                                .addOption(OptionType.INTEGER, "page", "Die Seite", false)
                );
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {

        Member member = event.getMember();

        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN) || Roles.hasMemberRole(member, Roles.DEVELOPER))) {

            event.reply("Du hast keine Rechte für diese Funktion!").setEphemeral(true).queue();

            return;
        }

        switch (Objects.requireNonNull(event.getSubcommandName()).toLowerCase(Locale.ROOT)) {
            case "add" -> {

                event.deferReply(true).queue();

                String twitchName = event.getOption("twitch-name", OptionMapping::getAsString);

                if (!this.twitchBot.addChannel(twitchName)) {

                    event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setAuthor("This Teebot")
                            .setDescription("Der Channel konnte nicht hinzugefügt werden! Ist der Channel eventuell bereits registriert?")
                            .setColor(Color.RED)
                            .build()).queue();

                    return;
                }

                event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor("This Teebot")
                        .setDescription("Der Channel wurde hinzugefügt!")
                        .setColor(Color.GREEN)
                        .build()).queue();


            }

            case "remove" -> {
                event.deferReply(true).queue();

                String twitchName = event.getOption("twitch-name", OptionMapping::getAsString);

                if (!this.twitchBot.removeChannel(twitchName)) {

                    event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setAuthor("This Teebot")
                            .setDescription("Der Channel konnte nicht entfernt werden! Ist der Channel eventuell bereits entfernt?")
                            .setColor(Color.RED)
                            .build()).queue();

                    return;
                }

                event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor("This Teebot")
                        .setDescription("Der Channel wurde entfernt!")
                        .setColor(Color.GREEN)
                        .build()).queue();
            }

            case "list" -> {

                event.deferReply(true).queue();

                var page = event.getOption("page", OptionMapping::getAsInt);
                if (null == page)
                    page = 1;

                List<TwitchChannel> twitchChannels = this.twitchBot.getTwitchDatabase().getAllTwitchChannels();

                HashMap<Integer, ArrayList<TwitchChannel>> paginatedTwitchChannels = new Paginator<>(twitchChannels).maxSizePerPage(10);

                EmbedBuilder embed = new EmbedBuilder()
                        .setAuthor("This Teebot")
                        .setColor(Color.WHITE)
                        .setTitle("Page " + page + " / " + paginatedTwitchChannels.keySet().size());

                if (!paginatedTwitchChannels.containsKey(page.intValue() - 1)) {

                    event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setAuthor("This Teebot")
                            .setDescription("Es gibt die Seite **nicht** die du angegeben hast!")
                            .setColor(Color.RED)
                            .setTimestamp(Instant.now())
                            .build()).queue();

                    return;
                }

                for (TwitchChannel twitchChannel : paginatedTwitchChannels.get(page.intValue() - 1)) {
                    embed.addField("Channel", twitchChannel.twitchChannel(), false);

                }

                event.getHook().editOriginalEmbeds(embed.build()).queue();

            }
        }

    }
}

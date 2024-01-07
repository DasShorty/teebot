package de.dasshorty.teebot.embedcreator;

import de.dasshorty.teebot.api.Paginator;
import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EmbedCommand implements SlashCommand {

    private final EmbedDatabase embedDatabase;

    public EmbedCommand(EmbedDatabase embedDatabase) {
        this.embedDatabase = embedDatabase;
    }

    @Override
    public CommandDataImpl commandData() {

        // Embed Create/Delete/Send/List

        return new CommandDataImpl("embed", "Verwalte Embeds")
                .addSubcommands(
                        new SubcommandData("create", "Erstelle ein Embed")
                                .addOption(OptionType.STRING, "embedid", "Die ID des Embeds", true)
                                .addOption(OptionType.BOOLEAN, "author", "Ob du als Autor angegeben werden sollst", true)
                                .addOption(OptionType.STRING, "title", "Der Titel des Embeds", false)
                                .addOption(OptionType.STRING, "description", "Die Beschreibung des Embeds", false),
                        new SubcommandData("delete", "Lösche ein Embed")
                                .addOption(OptionType.STRING, "embedid", "Gib die ID des Embeds an, dass gelöscht werden soll", true),
                        new SubcommandData("send", "Sende ein Embed in einen Channel")
                                .addOption(OptionType.STRING, "embedid", "Die ID des Embeds", true)
                                .addOption(OptionType.CHANNEL, "channel", "Der Channel wo das Embed reingesendet werden soll", true),
                        new SubcommandData("list", "Zeige dir alle gespeicherten Embeds an")
                                .addOption(OptionType.INTEGER, "page", "Welche Seite angezeigt werden soll", false)
                );
    }

    @Override
    public void onExecute(@NotNull SlashCommandInteractionEvent event) {

        Member member = event.getMember();

        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN, Roles.DEVELOPER))) {

            event.reply("Du hast keine Rechte für diese Funktion!").setEphemeral(true).queue();

            return;
        }


        switch (Objects.requireNonNull(event.getSubcommandName()).toLowerCase(Locale.ROOT)) {

            case "create" -> {

                event.deferReply(true).queue();
                String embedId = event.getOption("embedid", OptionMapping::getAsString);

                @SuppressWarnings("AutoBoxing")
                Boolean author = event.getOption("author", OptionMapping::getAsBoolean);

                // optional options

                String title = event.getOption("title", OptionMapping::getAsString);
                String description = event.getOption("description", OptionMapping::getAsString);

                Embed.Author authorTile = new Embed.Author(null, null, null);
                if (Boolean.TRUE.equals(author))
                    authorTile = new Embed.Author(member.getEffectiveName(), member.getEffectiveAvatarUrl(), null);

                Embed.Content contentTile = new Embed.Content(title, description);

                this.embedDatabase.storeEmbed(new Embed(
                        embedId,
                        authorTile,
                        contentTile,
                        new Embed.Fields(List.of()),
                        new Embed.Footer(null),
                        new Embed.Style(null, null, null, null)
                ));

                event.getHook().editOriginalEmbeds(new EmbedBuilder()
                                .setAuthor("Embed Creator")
                                .setDescription("Möchtest du den Autor ändern?")
                                .setColor(Color.WHITE)
                                .setTimestamp(Instant.now())
                                .setFooter("Step 1 / 4")
                                .build())
                        .setComponents(ActionRow.of(
                                Button.primary("embed-creator-step1", "Autor setzen"),
                                Button.secondary("embed-creator-step2", "Fortfahren mit Feldern (2/4)")
                        ))
                        .queue(message -> this.embedDatabase.getMemberEmbedMap().put(member.getId(), embedId));
            }

            case "list" -> {

                event.deferReply(true).queue();

                var page = event.getOption("page", OptionMapping::getAsInt);
                if (null == page)
                    page = 1;

                List<Embed> embeds = this.embedDatabase.embeds();

                HashMap<Integer, ArrayList<Embed>> paginatedEmbeds = new Paginator<>(embeds).maxSizePerPage(5);

                EmbedBuilder embed = new EmbedBuilder()
                        .setAuthor("Embed System")
                        .setColor(Color.WHITE)
                        .setTitle("Page " + page + " / " + paginatedEmbeds.keySet().size());

                if (!paginatedEmbeds.containsKey(page.intValue() - 1)) {

                    event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setAuthor("Embed System")
                            .setDescription("Es gibt die Seite **nicht** die du angegeben hast!")
                            .setColor(Color.RED)
                            .setTimestamp(Instant.now())
                            .build()).queue();

                    return;
                }

                for (Embed pageEmbed : paginatedEmbeds.get(page.intValue() - 1)) {
                    String title = pageEmbed.getContent().title();

                    embed.addField(pageEmbed.getEmbedId(), null == title ? "*Kein Titel gesetzt*" : title, false);

                }

                event.getHook().editOriginalEmbeds(embed.build()).queue();

            }

            case "send" -> {

                event.deferReply(true).queue();

                String embedId = event.getOption("embedid", OptionMapping::getAsString);
                GuildMessageChannel channel = Objects.requireNonNull(event.getOption("channel", OptionMapping::getAsChannel)).asGuildMessageChannel();

                CompletableFuture<Optional<Embed>> future = this.embedDatabase.getEmbed(embedId);

                try {
                    Optional<Embed> optionalEmbed = future.get();

                    if (optionalEmbed.isEmpty()) {
                        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                                .setAuthor("Embed System")
                                .setDescription("Das angegebene Embed mit der ID **" + embedId + "** existiert in der Datenbank nicht.")
                                .setColor(Color.RED)
                                .setTimestamp(Instant.now())
                                .build()).queue();
                        return;
                    }

                    Embed embed = optionalEmbed.get();

                    EmbedBuilder sendEmbed = embed.buildEmbed();

                    channel.sendMessageEmbeds(sendEmbed.build()).queue();
                    event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setAuthor("Embed System")
                            .setDescription("Das Embed wurde erfolgreich in den Channel " + channel.getAsMention() + " gesendet!")
                            .setColor(Color.GREEN)
                            .build()).queue();

                } catch (InterruptedException | ExecutionException e) {
                    e.fillInStackTrace();
                }
            }

            case "delete" -> {

                event.deferReply(true).queue();

                String embedId = event.getOption("embedid", OptionMapping::getAsString);

                this.embedDatabase.deleteEmbed(embedId);

                event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor("Embed System")
                        .setDescription("Das Embed wurde in der Datenbank gelöscht!")
                        .setColor(Color.GREEN)
                        .build()).queue();

            }

        }

    }

}

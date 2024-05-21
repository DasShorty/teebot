package de.dasshorty.teebot.embedcreator;

import com.google.gson.Gson;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import de.dasshorty.teebot.embedcreator.dto.EmbedDto;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.bson.types.ObjectId;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EmbedCommand implements SlashCommand {

    private final EmbedRepository embedRepository;
    private final EmbedManager embedManager;

    public EmbedCommand(EmbedManager embedManager) {
        this.embedRepository = embedManager.getEmbedRepo();
        this.embedManager = embedManager;
    }

    @Override
    public CommandDataImpl commandData() {

        return new CommandDataImpl("embed", "Verwalte die Embeds")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL))
                .addSubcommands(
                        new SubcommandData("create", "Erstelle ein neues Embed")
                                .addOption(OptionType.STRING, "desc", "Beschreibe das Embed", true)
                                .addOption(OptionType.ATTACHMENT, "embed", "Json Datei von embed.dan.onl", true),
                        new SubcommandData("delete", "Lösche ein Embed")
                                .addOption(OptionType.STRING, "id", "Die ID des Embeds", true),
                        new SubcommandData("list", "Liste dir die alle Embeds mit einer bestimmten Query")
                                .addOption(OptionType.STRING, "query", "Suche nach Embeds mit bestimmter Beschreibung", true),
                        new SubcommandData("send", "Send ein Embed in einen Channel")
                                .addOption(OptionType.CHANNEL, "channel", "Channel in welchen das Embed geschickt werden soll", true)
                                .addOption(OptionType.STRING, "id", "Die ID des Embeds", true)
                );
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {

        switch (event.getSubcommandName().toLowerCase()) {
            case "create" -> {

                event.deferReply(true).queue();
                Message.Attachment jsonFile = event.getOption("embed", OptionMapping::getAsAttachment);
                String desc = event.getOption("desc", OptionMapping::getAsString);

                try {

                    InputStream inputStream = URI.create(jsonFile.getUrl()).toURL().openStream();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                    StringBuilder builder = new StringBuilder();

                    int c = 0;
                    while ((c = bufferedReader.read()) != -1) {
                        builder.append((char) c);
                    }

                    EmbedDto embedDto = new Gson().fromJson(builder.toString(), EmbedDto.class);
                    embedDto.setEmbedId(new ObjectId().toString());
                    embedDto.setEmbedDescription(desc);
                    EmbedDto savedDto = this.embedRepository.save(embedDto);


                    event.getHook().editOriginal("Das Embed (**" + savedDto.getEmbedId() + "**) wurde erfolgreich in der Datenbank gespeichert!").queue();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
            case "delete" -> {

                event.deferReply(true).queue();
                String id = event.getOption("id", OptionMapping::getAsString);

                assert id != null;
                if (!this.embedRepository.existsById(id)) {
                    event.getHook().editOriginal("Das Embed (**" + id + "**) konnte nicht gefunden werden!").queue();
                    return;
                }

                this.embedRepository.deleteById(id);
                event.getHook().editOriginal("Das Embed wurde gelöscht!").queue();
            }

            case "send" -> {

                event.deferReply(true).queue();
                String id = event.getOption("id", OptionMapping::getAsString);
                GuildChannelUnion channel = event.getOption("channel", OptionMapping::getAsChannel);

                assert channel != null;

                if (!(channel instanceof GuildMessageChannel messageChannel)) {
                    event.getHook().editOriginal("In den angegebenen Channel können keine Nachrichten gesendet werden!").queue();
                    return;
                }

                EmbedBuilder builder = this.embedManager.buildEmbed(id);
                messageChannel.sendMessageEmbeds(builder.build()).queue(message -> {
                    event.getHook().editOriginal("Das Embed (" + message.getJumpUrl() + ") wurde in den Channel geschickt").queue();
                });
            }

            case "list" -> {

                event.deferReply(true).queue();
                String query = event.getOption("query", OptionMapping::getAsString);

                List<EmbedDto> embedDtos = this.embedManager.filteredByQuery(query);

                EmbedBuilder builder = new EmbedBuilder();

                embedDtos.forEach(embedDto -> builder.addField(embedDto.getEmbedId(), embedDto.getEmbedDescription(), true));

                event.getHook().editOriginalEmbeds(builder.setTitle("Alle Embeds mit query \"" + query + "\"")
                        .setDescription("Hier werden alle Embeds angezeigt, die als Embed Description die angegebenen Wörter beinhalten.")
                        .setColor(Color.WHITE).build()).queue();

            }
        }

    }
}

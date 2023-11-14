package de.dasshorty.teebot.tickets;

import com.google.gson.Gson;
import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import de.dasshorty.teebot.embedcreator.Embed;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class TicketCommand implements SlashCommand {

    private final EmbedDatabase embedDatabase;
    private final TicketDatabase ticketDatabase;

    public TicketCommand(EmbedDatabase embedDatabase, TicketDatabase ticketDatabase) {
        this.embedDatabase = embedDatabase;
        this.ticketDatabase = ticketDatabase;
    }

    @Override
    public CommandDataImpl commandData() {
        return new CommandDataImpl("tickets", "Tickets")
                .addSubcommandGroups(
                        new SubcommandGroupData("settings", "Einstellungen")
                                .addSubcommands(
                                        new SubcommandData("setup", "Setup Ticket Channel")
                                                .addOption(OptionType.CHANNEL, "ticket", "Channel to create tickets", true)
                                                .addOption(OptionType.STRING, "embed", "Id of embed to send", true)),
                        new SubcommandGroupData("management", "Tickets verwalten")
                                .addSubcommands(
                                        new SubcommandData("transcript", "Ticket History anzeigen")
                                                .addOption(OptionType.INTEGER, "ticket", "Die ID des Tickets, das angeschaut werden will", true)
                                )
                );
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {

        Member member = event.getMember();

        if (!Roles.hasMemberRole(member, Roles.DEVELOPER, Roles.ADMIN)) {
            event.reply("Du hast keine Rechte für diese Funktion!").setEphemeral(true).queue();
            return;
        }


        switch (Objects.requireNonNull(event.getSubcommandGroup())) {
            case "settings" -> {

                event.deferReply(true).queue();

                switch (event.getSubcommandName()) {
                    case "setup" -> {

                        GuildChannelUnion ticketChannel = event.getOption("ticket", OptionMapping::getAsChannel);

                        InteractionHook hook = event.getHook();

                        if (ticketChannel == null || ticketChannel.getType() != ChannelType.TEXT) {
                            hook.editOriginal("Der angegebene Channel ist kein Text Kanal!").queue();
                            return;
                        }

                        String embedId = event.getOption("embed", OptionMapping::getAsString);

                        if (embedId == null || embedId.isBlank()) {
                            hook.editOriginal("Die angegebene Embed Id ist nicht gültig!").queue();
                            return;
                        }

                        Optional<Embed> optionalEmbed = this.embedDatabase.getEmbed(embedId).join();

                        if (optionalEmbed.isEmpty()) {
                            hook.editOriginal("Die angegebene Embed Id konnte nicht gefunden werden!").queue();
                            return;
                        }

                        Embed embed = optionalEmbed.get();

                        ticketChannel.asTextChannel().sendMessageEmbeds(embed.buildEmbed().build()).addActionRow(Button.primary("ticket-create", "Erstelle ein Ticket")).queue();

                        hook.editOriginal("Das Embed wurde in den Kanal " + ticketChannel.getAsMention() + " geschickt!").queue();

                    }
                }
            }

            case "management" -> {

                switch (Objects.requireNonNull(event.getSubcommandName())) {

                    case "transcript" -> {

                        event.deferReply(true).queue();

                        int ticketId = Objects.requireNonNull(event.<Integer>getOption("ticket", OptionMapping::getAsInt)).intValue();

                        Optional<Ticket> optionalTicket = this.ticketDatabase.getTicketWithId((long) ticketId);

                        InteractionHook hook = event.getHook();

                        if (optionalTicket.isEmpty()) {

                            hook.editOriginal("Es konnte kein Ticket mit der angegebenen ID gefunden werden!").queue();

                            return;
                        }

                        Ticket ticket = optionalTicket.get();

                        hook.editOriginal("Ticket Transcript")
                                .setFiles(FileUpload.fromData(new Gson().toJson(ticket.messages()).getBytes(StandardCharsets.UTF_8), "Ticket-Transcript-" + ticket.ticketId() + ".json"))
                                .queue();
                    }


                }

            }
        }

    }
}

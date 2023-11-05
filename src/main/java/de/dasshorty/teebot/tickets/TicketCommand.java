package de.dasshorty.teebot.tickets;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import de.dasshorty.teebot.embedcreator.Embed;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.Optional;

public class TicketCommand implements SlashCommand {

    private final EmbedDatabase embedDatabase;

    public TicketCommand(EmbedDatabase embedDatabase) {
        this.embedDatabase = embedDatabase;
    }

    @Override
    public CommandDataImpl commandData() {
        return new CommandDataImpl("tickets", "Tickets")
                .addSubcommands(
                        new SubcommandData("setup", "Setup Ticket Channel")
                                .addOption(OptionType.CHANNEL, "ticket", "Channel to create tickets", true)
                                .addOption(OptionType.STRING, "embed", "Id of embed to send", true)
                );
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {

        Member member = event.getMember();

        if (!Roles.hasMemberRole(member, Roles.DEVELOPER, Roles.ADMIN)) {
            event.reply("Du hast keine Rechte für diese Funktion!").setEphemeral(true).queue();
            return;
        }

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
}

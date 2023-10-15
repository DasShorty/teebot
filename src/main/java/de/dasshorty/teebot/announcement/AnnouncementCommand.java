package de.dasshorty.teebot.announcement;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import de.dasshorty.teebot.embedcreator.Embed;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.awt.*;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


public class AnnouncementCommand implements SlashCommand {

    private final EmbedDatabase embedDatabase;

    public AnnouncementCommand(EmbedDatabase embedDatabase) {
        this.embedDatabase = embedDatabase;
    }

    @Override
    public CommandDataImpl commandData() {
        return new CommandDataImpl("announcement", "Sende ein Announcement")
                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "In welchen Channel soll das Announcement geschickt werden", true),
                        new OptionData(OptionType.STRING, "embedid", "Die Id des Embeds was als Announcement geschickt werden soll", true),
                        new OptionData(OptionType.ROLE, "role", "Die Rolle die beim Announcement gepingt werden soll.", true));
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {

        Member member = event.getMember();

        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN) || Roles.hasMemberRole(member, Roles.DEVELOPER))) {

            event.reply("Du hast keine Rechte für diese Funktion!").setEphemeral(true).queue();

            return;
        }

        event.deferReply(true).queue();

        GuildMessageChannel channel = event.getOption("channel", OptionMapping::getAsChannel).asGuildMessageChannel();
        String embedId = event.getOption("embedid", OptionMapping::getAsString);
        Role pingRole = event.getOption("role", OptionMapping::getAsRole);

        CompletableFuture<Optional<Embed>> future = this.embedDatabase.getEmbed(embedId);

        Optional<Embed> optionalEmbed = future.join();

        if (optionalEmbed.isEmpty()) {
            event.getHook().editOriginalEmbeds(new EmbedBuilder()
                    .setAuthor("Embed System")
                    .setDescription("Das angegebene Embed mit der ID **" + embedId + "** existiert in der Datenbank nicht.")
                    .setColor(Color.RED)
                    .setTimestamp(Instant.now())
                    .build()).queue();
            return;
        }

        channel.sendMessage(pingRole.getAsMention()).addEmbeds(optionalEmbed.get().buildEmbed().build()).queue();

        event.getHook().editOriginal("Das Announcement wurde versendet!").queue();

    }
}

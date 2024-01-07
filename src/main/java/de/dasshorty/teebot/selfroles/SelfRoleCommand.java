package de.dasshorty.teebot.selfroles;

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
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SelfRoleCommand implements SlashCommand {
    private final SelfRoleDatabase selfRoleDatabase;
    private final EmbedDatabase embedDatabase;

    public SelfRoleCommand(SelfRoleDatabase selfRoleDatabase, EmbedDatabase embedDatabase) {
        this.selfRoleDatabase = selfRoleDatabase;
        this.embedDatabase = embedDatabase;
    }

    @Override
    public CommandDataImpl commandData() {

        OptionData selfRoleCategory = new OptionData(OptionType.STRING, "category", "Kategorie der Eigenrolle", true);
        OptionData selfRole = new OptionData(OptionType.ROLE, "role", "Die Rolle", true);

        for (SelfRoleCategory value : SelfRoleCategory.values())
            selfRoleCategory.addChoice(value.getDisplayName(), value.name());

        return new CommandDataImpl("selfroles", "Eigenrollen verwalten")
                .addSubcommands(
                        new SubcommandData("add", "Füge eine Rolle hinzu")
                                .addOptions(selfRoleCategory, selfRole, new OptionData(OptionType.STRING, "name", "Der Name der Eigenrolle", true)),
                        new SubcommandData("remove", "Entferne eine Rolle als Self Role")
                                .addOptions(selfRole),
                        new SubcommandData("send", "Sende eine Gruppe an Selfroles in einen Channel")
                                .addOptions(selfRoleCategory,
                                        new OptionData(OptionType.CHANNEL, "channel", "In welchen Channel soll die Selfrole geschickt werden", true),
                                        new OptionData(OptionType.STRING, "embedid", "Die ID von dem Embed was gesendet werden soll", true),
                                        new OptionData(OptionType.BOOLEAN, "multiselect", "Soll man im Embed mehrere Sachen auswählen können", true))
                );
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN, Roles.DEVELOPER))) {

            event.reply("Du hast keine Rechte für diese Funktion!").setEphemeral(true).queue();

            return;
        }

        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "add" -> {
                event.deferReply(true).queue();

                Role role = event.getOption("role", OptionMapping::getAsRole);
                SelfRoleCategory category = SelfRoleCategory.valueOf(event.getOption("category", OptionMapping::getAsString));
                String name = event.getOption("name", OptionMapping::getAsString);

                assert role != null;

                if (!this.selfRoleDatabase.addSelfRole(new SelfRole(role.getId(), category, name))) {

                    event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setAuthor("Self Roles")
                            .setDescription("Etwas ist schiefgelaufen beim speichern der Self Role!")
                            .setColor(Color.RED)
                            .build()).queue();
                    return;

                }

                event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor("Self Roles")
                        .setDescription("Die Self Role wurde hinzugefügt!")
                        .setColor(Color.GREEN)
                        .build()).queue();

            }
            case "remove" -> {

                event.deferReply(true).queue();
                Role role = event.getOption("role", OptionMapping::getAsRole);

                if (!this.selfRoleDatabase.isSelfRolePersist(role.getId())) {
                    event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setAuthor("SelfRoles")
                            .setColor(Color.RED)
                            .setDescription("Die angegebene Rolle ist nicht als Selfrole regstriert!")
                            .build()).queue();
                    return;
                }

                this.selfRoleDatabase.removeSelfRole(role.getId());

                event.getHook().editOriginalEmbeds(new EmbedBuilder()
                        .setAuthor("SelfRoles")
                        .setColor(Color.GREEN)
                        .setDescription("Die angegebene Rolle ist nun nicht mehr Selfrole regstriert!")
                        .build()).queue();
            }

            case "send" -> {

                event.deferReply(true).queue();
                SelfRoleCategory category = SelfRoleCategory.valueOf(event.getOption("category", OptionMapping::getAsString));
                String embedId = event.getOption("embedid", OptionMapping::getAsString);
                Boolean multiAction = event.getOption("multiselect", OptionMapping::getAsBoolean);
                GuildMessageChannel channel = Objects.requireNonNull(event.getOption("channel", OptionMapping::getAsChannel)).asGuildMessageChannel();

                CompletableFuture<Optional<Embed>> futureEmbed = this.embedDatabase.getEmbed(embedId);

                try {
                    Optional<Embed> optionalEmbed = futureEmbed.get();

                    if (optionalEmbed.isEmpty()) {
                        event.getHook().editOriginalEmbeds(new EmbedBuilder()
                                .setAuthor("SelfRoles")
                                .setDescription("Die angegebene Embed Id ist in der Datenbank nicht bekannt! Hast du dich eventuell bei **" + embedId + "* vertippt?")
                                .setColor(Color.RED)
                                .build()).queue();
                        return;
                    }

                    Embed embed = optionalEmbed.get();

                    assert multiAction != null;
                    StringSelectMenu selectMenu = SelfRoleCategory.buildMenu(category, this.selfRoleDatabase, multiAction.booleanValue());

                    channel.sendMessageEmbeds(embed.buildEmbed().build()).addActionRow(selectMenu).queue();

                    event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setAuthor("Selfroles")
                            .setDescription("Das Selfroles Embed wurde erfolgreich in den Channel gesendet!")
                            .setColor(Color.GREEN)
                            .build()).queue();

                } catch (InterruptedException | ExecutionException e) {
                    e.fillInStackTrace();
                }

            }
        }

    }
}

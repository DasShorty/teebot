package de.dasshorty.teebot.warn;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class WarnCommand implements SlashCommand {

    private final  WarnManager warnManager;

    public WarnCommand(WarnManager warnManager) {
        this.warnManager = warnManager;
    }

    @Override
    public CommandDataImpl commandData() {

        OptionData optionData = new OptionData(OptionType.STRING, "reason", "Reason for the warn", true);
        for (WarnType value : WarnType.values()) {
            optionData.addChoice(value.getReason(), value.name());
        }

        return new CommandDataImpl("warn", "warn")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
                .addSubcommands(
                        new SubcommandData("add", "Add a warn to a user")
                                .addOption(OptionType.USER, "user", "The user to warn", true)
                                .addOptions(optionData)
                );
    }

    @Override
    public void onExecute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();

        assert member != null;

        if (!Roles.hasMemberRole(member, Roles.STAFF)) {
            event.reply("Du hast nicht die benötigten Rechte, um diese Aktion durchzuführen!").queue();
            return;
        }

        switch (event.getSubcommandName().toLowerCase()) {
            case "add" -> {

                Member suspect = event.getOption("user", OptionMapping::getAsMember);
                WarnType reason = WarnType.valueOf(event.getOption("reason", OptionMapping::getAsString));

                this.warnManager.warnMember(suspect, reason, member);

                event.getHook().editOriginal("Das Mitglied wurde gewarnt!").queue();

            }
        }
    }
}

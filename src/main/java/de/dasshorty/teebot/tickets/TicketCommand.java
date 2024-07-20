package de.dasshorty.teebot.tickets;

import com.google.gson.Gson;
import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.commands.slashcommands.SlashCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class TicketCommand implements SlashCommand {

    private final TicketRepository ticketRepo;

    public TicketCommand(TicketRepository ticketRepo) {
        this.ticketRepo = ticketRepo;
    }

    @Override
    public CommandDataImpl commandData() {
        return new CommandDataImpl("tickets", "Tickets")
                .addSubcommands(
                        new SubcommandData("transcript", "Ticket History anzeigen")
                                .addOption(OptionType.STRING, "ticket", "Die ID des Tickets, das angeschaut werden will", true)
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

            case "transcript" -> {

                event.deferReply(true).queue();

                String ticketId = Objects.requireNonNull(event.getOption("ticket", OptionMapping::getAsString));

                Optional<TicketDto> optional = this.ticketRepo.findById(ticketId);

                InteractionHook hook = event.getHook();

                if (optional.isEmpty()) {

                    hook.editOriginal("Es konnte kein Ticket mit der angegebenen ID gefunden werden!").queue();

                    return;
                }

                TicketDto ticketDto = optional.get();

                hook.editOriginal("Ticket Transript from " + ticketDto.getTicketId())
                        .setFiles(FileUpload.fromData(new Gson().toJson(ticketDto.getMessages()).getBytes(StandardCharsets.UTF_8),
                                "Ticket-Transcript-" + ticketDto.getTicketId() + ".json")).queue();

            }


        }

    }
}

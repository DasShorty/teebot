package de.dasshorty.teebot.embedcreator.steps.step2;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.modal.Modal;
import de.dasshorty.teebot.embedcreator.Embed;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FieldModal implements Modal {

    private final EmbedDatabase embedDatabase;

    public FieldModal(EmbedDatabase embedDatabase) {
        this.embedDatabase = embedDatabase;
    }

    @Override
    public String id() {
        return "embed-creator-step2";
    }

    @Override
    public void onExecute(ModalInteractionEvent event) {

        Member member = event.getMember();
        assert null != member;

        if (!(Roles.hasMemberRole(member, Roles.ADMIN) || Roles.hasMemberRole(member, Roles.DEVELOPER))) {
            event.reply("Du hast keine Rechte für diese Aktion!")
                    .setEphemeral(true).queue();
            return;
        }

        event.deferReply(true).queue();

        String embedId = this.embedDatabase.getMemberEmbedMap().get(member.getId());

        CompletableFuture<Optional<Embed>> future = this.embedDatabase.getEmbed(embedId);

        try {
            Optional<Embed> optionalEmbed = future.get();

            if (optionalEmbed.isEmpty()) {
                event.getHook().editOriginal("Etwas ist schiefgelaufen! Bitte Melde dies an <@&1159086761763414046> (FieldModal.java:39)").queue();
                return;
            }

            Embed embed = optionalEmbed.get();

            String title = Objects.requireNonNull(event.getValue("title")).getAsString();
            String description = Objects.requireNonNull(event.getValue("description")).getAsString();

            List<Embed.Field> fields = new ArrayList<>(embed.getFields().fields());

            fields.add(new Embed.Field(title, description, true));

            embed.setFields(new Embed.Fields(fields));

            this.embedDatabase.storeEmbed(embed);

            event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setAuthor("Embed Creator")
                            .setDescription("Das Feld wurde erfolgreich hinzugefügt")
                            .setColor(Color.WHITE)
                            .setTimestamp(Instant.now())
                            .setFooter("Step 2 / 4")
                            .build())
                    .queue(message -> this.embedDatabase.getMemberEmbedMap().put(member.getId(), embedId));

        } catch (InterruptedException | ExecutionException e) {
            e.fillInStackTrace();
        }
    }
}

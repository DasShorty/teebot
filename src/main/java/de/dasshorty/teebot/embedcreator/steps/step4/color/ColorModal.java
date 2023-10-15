package de.dasshorty.teebot.embedcreator.steps.step4.color;

import de.dasshorty.teebot.api.Roles;
import de.dasshorty.teebot.api.modal.Modal;
import de.dasshorty.teebot.embedcreator.Embed;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ColorModal implements Modal {
    private final EmbedDatabase embedDatabase;

    public ColorModal(EmbedDatabase embedDatabase) {
        this.embedDatabase = embedDatabase;
    }

    @Override
    public String id() {
        return "embed-creator-add-color";
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

        String embedId = this.embedDatabase.getMemberEmbedMap().get(member.getId());

        event.deferReply(true).queue();

        CompletableFuture<Optional<Embed>> future = this.embedDatabase.getEmbed(embedId);

        try {
            Optional<Embed> optionalEmbed = future.get();

            if (optionalEmbed.isEmpty()) {
                event.getHook().editOriginal("Etwas ist schiefgelaufen! Bitte Melde dies an <@&1159086761763414046> (ImageModal.java:45)").queue();
                return;
            }

            Embed embed = optionalEmbed.get();

            String color = Objects.requireNonNull(event.getValue("color")).getAsString();

            Embed.Style style = embed.getStyle();

            embed.setStyle(new Embed.Style(color, style.image(), style.thumbnail(), style.timestamp()));

            this.embedDatabase.storeEmbed(embed);

            event.getHook().editOriginalEmbeds(new EmbedBuilder()
                            .setAuthor("Embed Creator")
                            .setDescription("Farbe wurde gesetzt!")
                            .setColor(Color.WHITE)
                            .setTimestamp(Instant.now())
                            .setFooter("Step 4 / 4")
                            .build())
                    .queue(message -> this.embedDatabase.getMemberEmbedMap().put(member.getId(), embedId));

        } catch (InterruptedException | ExecutionException e) {
            e.fillInStackTrace();
        }
    }
}

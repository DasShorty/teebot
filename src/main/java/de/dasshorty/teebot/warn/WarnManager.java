package de.dasshorty.teebot.warn;

import de.dasshorty.teebot.DiscordBot;
import de.dasshorty.teebot.api.DiscordManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class WarnManager implements DiscordManager {

    private final WarnRepository warnRepository;

    public WarnManager(WarnRepository warnRepository) {
        this.warnRepository = warnRepository;
    }

    @Override
    public void setupDiscord(DiscordBot bot) {
        bot.getApiHandler().addSlashCommand(new WarnCommand(this));
    }

    void warnMember(Member suspect, WarnType reason, Member member) {

        Optional<UserWarnDto> optional = this.warnRepository.findById(suspect.getId());

        UserWarnDto dto;
        if (optional.isEmpty()) {
            dto = new UserWarnDto();
            dto.setUserId(suspect.getId());
        } else {
            dto = optional.get();
        }

        WarnDto warn = new WarnDto();
        warn.setType(reason);
        warn.setPunisherId(member.getId());
        warn.setSubmitted(System.currentTimeMillis());

        WarnDto lastWarn = this.getLastWarn(dto);

        WarnPunishment punishment;

        if (lastWarn == null) {
            punishment = WarnPunishment.NONE;
        } else {

            int warnCount = this.getWarnCountByPunishment(dto, lastWarn);

            if (warnCount >= lastWarn.getPunishment().getCount()) {
                punishment = WarnPunishment.getNextPunishment(lastWarn.getPunishment());
            } else {
                punishment = lastWarn.getPunishment();
            }

        }

        warn.setPunishment(punishment);
        dto.getWarns().add(warn);
        this.warnRepository.save(dto);
        this.executePunishment(suspect, warn);
    }

    private String getReason(Member member, WarnPunishment punishment) {
        switch (punishment) {

            case MUTE -> {
                member.mute(true).queue();
                return "Du wurdest gemuted. Du kannst nun nicht mehr in Sprachkanälen reden. Dieser wird automatisch nach " + 24 * punishment.getCount() + "h aufgehoben";
            }
            case TIMEOUT -> {
                member.timeoutFor(Duration.ofHours((long) punishment.getCount())).queue();
                return "Du wurdest getimeoutet. Du kannst nun nicht mehr an Sprachkanälen oder im Chat teilnehmen. Der Timeout erlischt in " +
                        24 * punishment.getCount() + "h!";
            }

            default -> {
                return "Dir passiert jetzt erstmal nichts, solltest du jedoch öfters auffallen erhöht sich die Warnstufe und es werden schlimmere Strafen verhängt";
            }

        }
    }

    private void executePunishment(Member member, WarnDto warn) {

        User user = member.getUser();
        user.openPrivateChannel().queue(privateChannel -> {

            privateChannel.sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("Neue Warnung")
                    .setDescription("Du wurdest auf dem Teepott Discord Server verwarnt. Grund: " + warn.getType().getReason())
                    .addField("Was sind jetzt die Folgen?", this.getReason(member, warn.getPunishment()), false)
                    .addField("Was habe ich gemacht?", "Wenn du denkst, dass der Warn nicht richtig war, kontaktiere den Support via <#836107968659456000>", false)
                    .setColor(Color.RED)
                    .setTimestamp(Instant.now())
                    .build()).queue();
        });

        // log warns
        member.getGuild().getTextChannelById("1246827888896901211").sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Warnungen")
                .setDescription("Neue Warnung von <@" + warn.getPunisherId() + ">")
                .addField("Mitglied", member.getAsMention(), false)
                .addField("Grund", warn.getType().getReason(), false)
                .addField("Strafe", warn.getPunishment().name().toUpperCase(), false)
                .addField("Strafe erlitten", String.valueOf(warn.getPunishment().getCount()), true)
                .setTimestamp(Instant.now())
                .setColor(Color.ORANGE)
                .build()).queue();

    }

    private int getWarnCountByPunishment(UserWarnDto dto, WarnDto warn) {
        return dto.getWarns().stream().filter(warnDto -> warnDto.getType() == warn.getType()).toList().size();
    }

    private @Nullable WarnDto getLastWarn(UserWarnDto dto) {
        if (dto.getWarns().isEmpty()) {
            return null;
        }
        return dto.getWarns().get(dto.getWarns().size() - 1);
    }
}

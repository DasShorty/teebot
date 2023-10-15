package de.dasshorty.teebot.membercounter;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UpdateMemberCounter {

    public UpdateMemberCounter(Guild guild) {

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            VoiceChannel voiceChannelById = guild.getVoiceChannelById("1159969176614604821");

            assert voiceChannelById != null;
            voiceChannelById.getManager().setName("Members: » " + guild.getMemberCount()).queue();

        }, 0L, 3L, TimeUnit.SECONDS);
    }
}

package de.dasshorty.teebot.welcomeembed;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SendWelcomeEmbed extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        val member = event.getMember();

        val guild = event.getGuild();

        val welcomeChannel = guild.getTextChannelById("1159845675341713469");

        assert welcomeChannel != null;
        welcomeChannel.sendMessageEmbeds(new EmbedBuilder()
                .setDescription("Cooles Welcome Embed!")
                .build()).queue();

    }
}

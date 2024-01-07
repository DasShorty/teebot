package de.dasshorty.teebot.welcomeembed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SendWelcomeEmbed extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        Member member = event.getMember();

        Guild guild = event.getGuild();

        TextChannel welcomeChannel = guild.getTextChannelById("835974825360883764");

        assert welcomeChannel != null;
        welcomeChannel.sendMessageEmbeds(new EmbedBuilder()
                .setDescription("Cooles Welcome Embed!")
                .build()).queue();

    }
}

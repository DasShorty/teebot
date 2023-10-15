package de.dasshorty.teebot.jtc;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JTCVoiceListener extends ListenerAdapter {
    private final JTCDatabase jtcDatabase;

    public JTCVoiceListener(JTCDatabase jtcDatabase) {
        this.jtcDatabase = jtcDatabase;
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {


    }
}

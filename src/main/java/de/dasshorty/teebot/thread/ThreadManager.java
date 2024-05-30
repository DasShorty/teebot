package de.dasshorty.teebot.thread;

import de.dasshorty.teebot.DiscordBot;
import de.dasshorty.teebot.api.DiscordManager;

public class ThreadManager implements DiscordManager {
    @Override
    public void setupDiscord(DiscordBot bot) {
        bot.getBuilder().addEventListeners(new AutoThreadListener());
    }
}

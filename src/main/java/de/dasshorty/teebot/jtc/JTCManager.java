package de.dasshorty.teebot.jtc;

import de.dasshorty.teebot.DiscordBot;
import de.dasshorty.teebot.api.DiscordManager;
import de.dasshorty.teebot.jtc.button.ChangeTitleButton;
import de.dasshorty.teebot.jtc.button.EnterNewTitleModal;

public class JTCManager implements DiscordManager {
    @Override
    public void setupDiscord(DiscordBot bot) {
        bot.getBuilder().addEventListeners(new JTCVoiceListener(this.jtcRepo));
        bot.getApiHandler().addButton(new ChangeTitleButton(this.jtcRepo));
        bot.getApiHandler().addModal(new EnterNewTitleModal(this.jtcRepo));
    }

    private final JTCRepository jtcRepo;

    public JTCManager(JTCRepository jtcRepo) {
        this.jtcRepo = jtcRepo;
    }
}

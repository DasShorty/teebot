package de.dasshorty.teebot.giveaways;

import de.dasshorty.teebot.DiscordBot;
import de.dasshorty.teebot.api.DiscordManager;
import net.dv8tion.jda.api.entities.Guild;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GiveawayManager implements DiscordManager {

    private final GiveawayRepository giveawayRepo;
    private final Guild guild;

    public GiveawayManager(GiveawayRepository giveawayRepo, Guild guild) {
        this.giveawayRepo = giveawayRepo;
        this.guild = guild;

        this.runTask();
    }

    private void runTask() {

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {

            for (GiveawayDto giveaway : this.giveawayRepo.getAllByActiveIsTrue()) {
                if (giveaway.getEndTimeCalculated() >= Instant.now().getEpochSecond()) {

                    if (giveaway.isActive()) {

                        giveaway.setActive(false);
                        this.giveawayRepo.save(giveaway);
                        giveaway.endGiveaway(this.guild);

                    }

                }
            }
        }, 0L, 5L, TimeUnit.SECONDS);

    }

    @Override
    public void setupDiscord(DiscordBot bot) {
        bot.getApiHandler().addButton(new EnterGiveawayButton(this.giveawayRepo));
        bot.getApiHandler().addSlashCommand(new GiveawayCommand(this.giveawayRepo, this));
    }
}

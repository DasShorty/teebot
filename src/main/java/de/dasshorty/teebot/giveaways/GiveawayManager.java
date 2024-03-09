package de.dasshorty.teebot.giveaways;

import net.dv8tion.jda.api.entities.Guild;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GiveawayManager {

    private final GiveawayDatabase giveawayDatabase;
    private final Guild guild;

    public GiveawayManager(GiveawayDatabase giveawayDatabase, Guild guild) {
        this.giveawayDatabase = giveawayDatabase;
        this.guild = guild;

        this.runTask();
    }

    private void runTask() {

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {

            this.giveawayDatabase.getActiveGiveaways().forEach(giveaway -> {

                if (giveaway.getEndTimeCalculated() >= Instant.now().getEpochSecond()) {

                    if (giveaway.active()) {

                        Giveaway updatedGiveaway = giveaway.setActive(false);
                        this.giveawayDatabase.updateGiveaway(updatedGiveaway);
                        updatedGiveaway.endGiveaway(this.guild);

                    }

                }

            });

        }, 0L, 5L, TimeUnit.SECONDS);

    }
}

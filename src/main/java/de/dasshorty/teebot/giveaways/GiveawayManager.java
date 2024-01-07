package de.dasshorty.teebot.giveaways;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GiveawayManager {

    private final GiveawayDatabase giveawayDatabase;
    private final Guild guild;

    public GiveawayManager(GiveawayDatabase giveawayDatabase, Guild guild) {
        this.giveawayDatabase = giveawayDatabase;
        this.guild = guild;

        this.startUpRunnable();
    }

    private void startUpRunnable() {

        this.giveawayDatabase.getActiveGiveaways().forEach(this::createScheduledTask);

    }

    void createScheduledTask(@NotNull Giveaway giveaway) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {

            Giveaway updatedGiveaway = giveaway.setActive(false);

            this.giveawayDatabase.updateGiveaway(updatedGiveaway);

            updatedGiveaway.endGiveaway(this.guild);

        }, giveaway.getTimeUntilEnd(), TimeUnit.SECONDS);
    }
}

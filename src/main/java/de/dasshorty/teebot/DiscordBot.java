package de.dasshorty.teebot;

import de.dasshorty.teebot.api.APIHandler;
import de.dasshorty.teebot.embedcreator.EmbedManager;
import de.dasshorty.teebot.embedcreator.EmbedRepository;
import de.dasshorty.teebot.giveaways.GiveawayManager;
import de.dasshorty.teebot.giveaways.GiveawayRepository;
import de.dasshorty.teebot.jtc.JTCManager;
import de.dasshorty.teebot.jtc.JTCRepository;
import de.dasshorty.teebot.notification.twitch.TwitchBot;
import de.dasshorty.teebot.thread.ThreadManager;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Service
public class DiscordBot {

    private final JDABuilder builder;
    private final APIHandler apiHandler;

    @Autowired
    public DiscordBot(EmbedRepository embedRepository, JTCRepository jtcRepository, GiveawayRepository giveawayRepository) {
        OkHttpClient httpClient = new OkHttpClient();

        this.builder = JDABuilder.createDefault(System.getenv("BOT_TOKEN"));

        this.builder.setMemberCachePolicy(MemberCachePolicy.ALL);

        this.builder.setActivity(Activity.customStatus("Trinkt Kaffee"));

        this.builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES);
        this.builder.enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);

        this.apiHandler = new APIHandler(this.builder);

        new EmbedManager(embedRepository).setupDiscord(this);
        new JTCManager(jtcRepository).setupDiscord(this);
        new ThreadManager().setupDiscord(this);

        try {
            JDA jda = this.builder.setAutoReconnect(true).build().awaitReady();
            Guild guild = jda.getGuilds().get(0);


            new GiveawayManager(giveawayRepository, guild).setupDiscord(this);
            new TwitchBot(guild);

            this.apiHandler.getCommandHandler().updateCommands(guild);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
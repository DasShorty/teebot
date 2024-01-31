package de.dasshorty.teebot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import com.github.twitch4j.helix.domain.Stream;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;

public class TwitchBot {

    private final TwitchDatabase twitchDatabase;
    private final HashMap<String, Message> notifications = new HashMap<>();
    private TwitchClient client;

    public TwitchBot(TwitchDatabase twitchDatabase, Guild guild) {
        this.twitchDatabase = twitchDatabase;

        String twitchAccessToken = System.getenv("TWITCH_ACCESS_TOKEN");
        OAuth2Credential twitch = new OAuth2Credential("twitch", twitchAccessToken);
        this.client = TwitchClientBuilder
                .builder()
                .withDefaultAuthToken(twitch)
                .withChatAccount(twitch)
                .withEnableHelix(true)
                .withEnableChat(true)
                .build();

        this.loadTwitchChannel();

        this.client.getEventManager().onEvent(ChannelGoOfflineEvent.class, channelGoOfflineEvent -> {

            String twitchName = channelGoOfflineEvent.getChannel().getName();
            System.out.println("Stream stoppt von " + twitchName);

            if (!this.notifications.containsKey(twitchName))
                return;

            this.notifications.get(twitchName).delete().queue();
            this.notifications.remove(twitchName);
        });

        this.client.getEventManager()
                .onEvent(ChannelGoLiveEvent.class, channelGoLiveEvent -> {

                    System.out.println("Stream startet von " + channelGoLiveEvent.getChannel().getName());

                    NewsChannel textChannelById = guild.getNewsChannelById("1096139850429771917");

                    Stream stream = channelGoLiveEvent.getStream();
                    String twitchName = channelGoLiveEvent.getChannel().getName();

                    String url = "https://twitch.tv/" + twitchName;

                    if (textChannelById == null) {
                        System.out.println("Textchannel is null");
                        return;
                    }

                    textChannelById.sendMessage("<@&835994615399317534>")
                            .addEmbeds(new EmbedBuilder()
                                    .setAuthor(twitchName, url)
                                    .setTitle(stream.getTitle())
                                    .setDescription(twitchName + " ist Live gegangen! Es wird heute " + stream.getGameName() + " gestreamt!")
                                    .setImage(stream.getThumbnailUrl())
                                    .setTimestamp(Instant.now())
                                    .setColor(Color.decode("#a970ff"))
                                    .build()).addActionRow(Button.link(url, "Stream anschauen")).flatMap(Message::crosspost).queue(message -> this.notifications.put(twitchName, message));
                    System.out.println(twitchName);
                });
    }

    TwitchDatabase getTwitchDatabase() {
        return this.twitchDatabase;
    }

    void sendMessage(String msg) {
        synchronized (this) {
            this.twitchDatabase.getAllTwitchChannels().forEach(channel -> {
                this.client.getChat().sendMessage(channel.twitchChannel(), msg);
            });
        }
    }

    private void loadTwitchChannel() {
        this.twitchDatabase.getAllTwitchChannels().forEach(channel -> {
            this.client.getClientHelper().enableStreamEventListener(channel.twitchChannel());
            this.client.getChat().joinChannel(channel.twitchChannel());
            this.client.getChat().sendMessage(channel.twitchChannel(), "Der Teebot ist erfolgreich deinem Chat beigetreten!");
            System.out.println("Logged in into " + channel.twitchChannel() + " Channel!");
        });
    }

    boolean addChannel(String channelName) {

        if (this.twitchDatabase.isChannelRegistered(channelName))
            return false;

        this.client.getClientHelper().enableStreamEventListener(channelName);
        this.twitchDatabase.addChannel(new TwitchChannel(channelName));
        this.client.getChat().joinChannel(channelName);
        this.client.getChat().sendMessage(channelName, "Der Teebot ist erfolgreich deinem Chat beigetreten!");
        System.out.println("Added " + channelName + " Twitch channel!");

        return true;
    }

    public boolean removeChannel(String channelName) {

        if (!this.twitchDatabase.isChannelRegistered(channelName))
            return false;

        this.client.getClientHelper().disableStreamEventListener(channelName);
        this.twitchDatabase.removeChannel(channelName);
        System.out.println("Removed " + channelName + " Twitch channel!");
        return true;
    }
}

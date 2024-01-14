package de.dasshorty.teebot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.helix.domain.Stream;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.Instant;

public class TwitchBot {

    private final TwitchDatabase twitchDatabase;
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

        this.client.getEventManager()
                .onEvent(ChannelGoLiveEvent.class, channelGoLiveEvent -> {

                    System.out.println("Stream startet von " + channelGoLiveEvent.getChannel().getName());


                    TextChannel textChannelById = guild.getTextChannelById("1096139850429771917");

                    Stream stream = channelGoLiveEvent.getStream();
                    String twitchName = channelGoLiveEvent.getChannel().getName();

                    this.client.getChat().sendMessage(twitchName, "Du bist Live gegangen! Eine benachrichtigung wurde in den Livechat gesendet!");

                    String url = "https://twitch.tv/" + twitchName;
                    assert textChannelById != null;
                    textChannelById.sendMessageEmbeds(new EmbedBuilder()
                            .setAuthor(twitchName, url)
                            .setTitle(stream.getTitle())
                            .setDescription(twitchName + " ist Live gegangen! Es wird heute " + stream.getGameName() + " gestreamt!")
                            .setImage(stream.getThumbnailUrl())
                            .setTimestamp(Instant.now())
                            .setColor(Color.decode("#a970ff"))
                            .build()).addActionRow(Button.link(url, "Stream anschauen")).flatMap(Message::crosspost).queue();

                    System.out.println(twitchName);

                });
    }

    public TwitchDatabase getTwitchDatabase() {
        return this.twitchDatabase;
    }

    synchronized void sendMessage(String msg) {
        this.twitchDatabase.getAllTwitchChannels().forEach(channel -> {
            this.client.getChat().sendMessage(channel.twitchChannel(), msg);
        });
    }

    private void loadTwitchChannel() {
        this.twitchDatabase.getAllTwitchChannels().forEach(channel -> {
            this.client.getClientHelper().enableStreamEventListener(channel.twitchChannel());
            this.client.getChat().joinChannel(channel.twitchChannel());
            this.client.getChat().sendMessage(channel.twitchChannel(), "Der Teebot ist erfolgreich deinem Chat beigetreten!");
            System.out.println("Logged in into " + channel.twitchChannel() + " Channel!");
        });
    }

    public boolean addChannel(String channelName) {

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

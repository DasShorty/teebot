package de.dasshorty.teebot.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.Instant;

public class TwitchBot {

    @Getter
    private final TwitchDatabase twitchDatabase;
    private TwitchClient client;

    public TwitchBot(TwitchDatabase twitchDatabase, Guild guild) {
        this.twitchDatabase = twitchDatabase;

        val twitchAccessToken = System.getenv("TWITCH_ACCESS_TOKEN");
        this.client = TwitchClientBuilder
                .builder()
                .withDefaultAuthToken(new OAuth2Credential("thisteebot", twitchAccessToken))
                .withEnableHelix(true)
                .withEnableChat(true)
                .build();

        this.loadTwitchChannel();

        this.client.getEventManager()
                .onEvent(ChannelGoLiveEvent.class, channelGoLiveEvent -> {

                    System.out.println("Stream startet von " + channelGoLiveEvent.getChannel().getName());


                    val textChannelById = guild.getTextChannelById("1160168436224241705");

                    val stream = channelGoLiveEvent.getStream();
                    val twitchName = channelGoLiveEvent.getChannel().getName();

                    this.client.getChat().sendMessage(twitchName, "Du bist Live gegangen! Eine benachrichtigung wurde in den Livechat gesendet!");

                    val url = "https://twitch.tv/" + twitchName;
                    assert textChannelById != null;
                    textChannelById.sendMessageEmbeds(new EmbedBuilder()
                            .setAuthor(twitchName, url)
                            .setTitle(stream.getTitle())
                            .setDescription(twitchName + " ist Live gegangen! Es wird heute " + stream.getGameName() + " gestreamt!")
                            .setImage(stream.getThumbnailUrl())
                            .setTimestamp(Instant.now())
                            .setColor(Color.decode("#a970ff"))
                            .build()).addActionRow(Button.link(url, "Stream anschauen")).queue();

                    System.out.println(twitchName);

                });
    }

    private void loadTwitchChannel() {
        this.twitchDatabase.getAllTwitchChannels().forEach(channel -> {
            this.client.getClientHelper().enableStreamEventListener(channel.twitchChannel());
            this.client.getChat().joinChannel(channel.twitchChannel());
            this.client.getChat().sendMessage(channel.twitchChannel(), "Der Teebot ist erfolgreich deinem Chat beigetreten!");
        });
    }

    public boolean addChannel(String channelName) {

        if (this.twitchDatabase.isChannelRegistered(channelName))
            return false;

        this.client.getClientHelper().enableStreamEventListener(channelName);
        this.twitchDatabase.addChannel(new TwitchChannel(channelName));
        this.client.getChat().joinChannel(channelName);
        this.client.getChat().sendMessage(channelName, "Der Teebot ist erfolgreich deinem Chat beigetreten!");

        return true;
    }

    public boolean removeChannel(String channelName) {

        if (!this.twitchDatabase.isChannelRegistered(channelName))
            return false;

        this.client.getClientHelper().disableStreamEventListener(channelName);
        this.twitchDatabase.removeChannel(channelName);

        return true;
    }
}

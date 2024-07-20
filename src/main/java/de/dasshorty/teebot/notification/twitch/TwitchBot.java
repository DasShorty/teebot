package de.dasshorty.teebot.notification.twitch;

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

    private final HashMap<String, Message> notifications = new HashMap<>();
    private TwitchClient client;

    public TwitchBot(Guild guild) {
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

                    textChannelById.sendMessage("<@&1216029999702741023>")
                            .addEmbeds(new EmbedBuilder()
                                    .setAuthor(twitchName, url)
                                    .setTitle(stream.getTitle())
                                    .setDescription(twitchName + " ist Live gegangen! Es wird heute " + stream.getGameName() + " gestreamt!")
                                    .setThumbnail(stream.getThumbnailUrl())
                                    .setTimestamp(Instant.now())
                                    .setColor(Color.decode("#a970ff"))
                                    .setFooter("Twitch", "https://cdn.dasshorty.de/twitch.png")
                                    .build()).addActionRow(Button.link(url, "Stream anschauen")).flatMap(Message::crosspost).queue(message -> this.notifications.put(twitchName, message));
                    System.out.println(twitchName);
                });
    }

    void sendMessage(String msg) {
        this.client.getChat().sendMessage("laudytv", msg);
    }

    private void loadTwitchChannel() {

        this.client.getClientHelper().enableStreamEventListener("laudytv");
        this.client.getChat().joinChannel("laudytv");
        this.client.getChat().sendMessage("laudytv", "Der Teebot ist erfolgreich deinem Chat beigetreten!");

        System.out.println("Logged in into LaudyTV Channel!");
    }
}

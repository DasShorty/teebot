package de.dasshorty.teebot.notification.youtube;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public record YoutubeNotificationManager(YoutubeNotificationDatabase database, OkHttpClient httpClient) {

    private static final String API_KEY = System.getenv("GOOGLE_YT_DATA_API");


    public void initCheck(Guild guild) {

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {

            try {

                String lastVideoId = this.getLastVideoId();
                this.sendNotifications(guild, lastVideoId);

            } catch (IOException e) {
                e.fillInStackTrace();
            }

        }, 0L, 1L, TimeUnit.MINUTES);
    }

    private void sendNotifications(Guild guild, String lastVideoId) throws IOException {
        if (!this.database.compareIds(lastVideoId)) {
            this.database.setLastVideoId(lastVideoId);
            Optional<VideoData> videoData = this.getVideoData(lastVideoId);

            if (videoData.isEmpty()) {
                return;
            }

            this.sendNotification(guild, videoData.get());
        }
    }

    private void sendNotification(Guild guild, VideoData data) {

        NewsChannel socialMediaChannel = guild.getNewsChannelById("1096139850429771917");

        MessageEmbed embed = new EmbedBuilder()
                .setAuthor("LaudyTV", "https://youtube.com/@LaudyTV", "https://yt3.googleusercontent.com/1x6uW3I-UTuU__oqnTqJv_Wq_dXaozZABBVtRmyyb_euRNKWxGGXVFZEW6kS6dwFxfCSxfbdOZU=s176-c-k-c0x00ffffff-no-rj")
                .setTitle(data.title())
                .setDescription(data.description())
                .setImage(data.thumbnail())
                .setColor(Color.RED)
                .setTimestamp(Instant.now())
                .setFooter("Youtube Uploads", "https://cdn.dasshorty.de/youtube-logo.png")
                .build();


        assert socialMediaChannel != null;
        socialMediaChannel.sendMessage("<@&835994615399317534>")
                .addEmbeds(embed)
                .addActionRow(Button.link(data.viewLink(), "Video anschauen")).flatMap(Message::crosspost).queue();

    }

    private Optional<VideoData> getVideoData(String videoId) throws IOException {

        Request request = new Request.Builder()
                .get()
                .url("https://youtube.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&id=" + videoId + "&maxResults=1&key=" + API_KEY)
                .build();

        Response execute = this.httpClient.newCall(request).execute();

        if (execute.code() != 200) {
            return Optional.empty();
        }

        JsonObject json = JsonParser.parseString(execute.body().string()).getAsJsonObject().getAsJsonArray("items")
                .get(0).getAsJsonObject().getAsJsonObject("snippet");

        String title = json.get("title").getAsString();
        String description = json.get("description").getAsString();
        String thumbnailUrl = json.getAsJsonObject("thumbnails")
                .getAsJsonObject("standard").get("url").getAsString();

        return Optional.of(new VideoData(title, description, thumbnailUrl, "https://www.youtube.com/watch?v=" + videoId));
    }

    private String getLastVideoId() throws IOException {

        String channelId = "UCROruaUtqgvZa9EQQZgdw7g";

        Request request = new Request.Builder()
                .get()
                .url("https://youtube.googleapis.com/youtube/v3/activities?part=snippet%2CcontentDetails&channelId=" + channelId + "&maxResults=1&key=" + API_KEY)
                .build();

        Response execute = this.httpClient.newCall(request)
                .execute();

        if (execute.code() != 200) {
            return null;
        }

        if (execute.body() == null) {
            return null;
        }

        JsonObject json = JsonParser.parseString(execute.body().string()).getAsJsonObject().getAsJsonArray("items").get(0).getAsJsonObject();

        return json.getAsJsonObject("contentDetails").getAsJsonObject("upload").get("videoId").getAsString();
    }

}

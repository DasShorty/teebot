package de.dasshorty.teebot.welcomeembed;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class SendWelcomeEmbed extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        Member member = event.getMember();

        Guild guild = event.getGuild();

        TextChannel welcomeChannel = guild.getTextChannelById("835974825360883764");

        BufferedImage bufferedImage = this.generateWelcomeImage(member.getEffectiveName(), event.getGuild().getMemberCount(), member.getEffectiveAvatarUrl());
        if (bufferedImage == null)
            return;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(bufferedImage, "png", baos);

            assert welcomeChannel != null;

            welcomeChannel.sendMessage("Hey " + member.getAsMention() + "! Willkommen auf **" + guild.getName() + "**")
                    .addFiles(FileUpload.fromData(baos.toByteArray(), "welcome.png"))
                    .queue();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private BufferedImage generateWelcomeImage(String username, int member, String avatarUrl) {
        try {
            // Laden des Avatar-Bildes von der URL
            BufferedImage avatar = ImageIO.read(new URL(avatarUrl));

            // Erstellen einer Hintergrund-Bild-Datei
            BufferedImage background = new BufferedImage(1150, 500, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = background.createGraphics();

            // Hintergrund zeichnen
            g.setColor(Color.decode("#212121"));
            g.fillRect(0, 0, 1150, 500);

            // Hintergrund zeichnen
            g.setColor(Color.decode("#262626"));
            g.fillRect(50, 50, 1050, 400);

            // Avatar zeichnen (als Kreis)
            int avatarSize = 200;
            int avatarSize2 = 220;
            g.setClip(new Ellipse2D.Float(120, 130, avatarSize2, avatarSize2));
            g.drawImage(avatar, 140, 150, avatarSize, avatarSize, null);
            g.setClip(null);

            int startSize = 455;
            int startSize2 = 245;
            // Text zeichnen
            g.setColor(Color.WHITE);
            g.setFont(new Font("Segoe UI", Font.PLAIN, 40));
            g.drawString("Willkommen " + username + "!", startSize, startSize2);

            g.setFont(new Font("Segoe UI Light", Font.BOLD, 30));
            g.setColor(Color.decode("#CECECE"));
            g.drawString("Member auf dem Discord: " + member, startSize, startSize2 + 50);

            return background;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

package de.dasshorty.teebot.jtc;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JTCVoiceListener extends ListenerAdapter {

    private final JTCRepository jtcRepo;
    private final List<Category> categories = new ArrayList<>();

    public JTCVoiceListener(JTCRepository jtcRepo) {
        this.jtcRepo = jtcRepo;
    }

    private static void createCategory(@NotNull Guild guild, Consumer<Category> consumer) {
        guild.createCategory("\uD83D\uDD0A│TEMP TALKS").setPosition(1).queue(consumer);
    }

    private static void logInChannel(Guild guild, String message) {

        guild.getTextChannelById("1163056990369628180").sendMessageEmbeds(new EmbedBuilder()
                .setDescription(message)
                .setTimestamp(Instant.now())
                .build()).queue();

    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {

        Member member = event.getMember();

        if (event.getChannelLeft() != null) {
            this.removeVoiceChannel(event, member);
        } else if (event.getChannelJoined() != null) {
            this.addVoiceChannel(event, member);
        }
    }

    private void addVoiceChannel(GuildVoiceUpdateEvent event, Member member) {

        assert event.getChannelJoined() != null;

        if (!event.getChannelJoined().getId().equals("1159847520759984128"))
            return;

        Guild guild = event.getGuild();

        this.createTalk(guild, member, voiceChannel -> {

            this.logInChannel(guild, "Channel " + voiceChannel.getName() + " has been created!");

            guild.moveVoiceMember(member, voiceChannel).queue();
        });
    }

    private void removeVoiceChannel(GuildVoiceUpdateEvent event, Member member) {

        AudioChannelUnion channelLeft = event.getChannelLeft();
        assert channelLeft != null;

        val optional = this.jtcRepo.findByChannelId(channelLeft.getId());

        if (optional.isEmpty()) {
            return;
        }

        if (!channelLeft.getMembers().isEmpty()) {
            return;
        }

        this.jtcRepo.delete(optional.get());

        this.logInChannel(channelLeft.getGuild(), "Channel " + channelLeft.getName() + " has been deleted!");

        Category parentCategory = channelLeft.getParentCategory();

        channelLeft.delete().queue();

        assert parentCategory != null;

        if (parentCategory.getChannels().size() == 1) {
            this.categories.remove(parentCategory);
            parentCategory.delete().queue();
        }

    }

    private void createTalk(Guild guild, Member member, Consumer<VoiceChannel> onChannelCreated) {

        if (this.categories.isEmpty()) {
            createCategory(guild, category1 -> {
                this.createVoiceChannel(category1, member, onChannelCreated);
                this.categories.add(category1);
            });
            return;
        }

        Category category = this.categories.get(this.categories.size() - 1);

        if (category.getChannels().size() == 50) {
            createCategory(guild, category1 -> {
                this.createVoiceChannel(category1, member, onChannelCreated);
                this.categories.add(category1);
            });
            return;
        }

        this.createVoiceChannel(category, member, onChannelCreated);

    }

    private void createVoiceChannel(Category category, Member member, Consumer<VoiceChannel> onChannelCreated) {
        category.createVoiceChannel(member.getEffectiveName() + "'s Talk").queue(voiceChannel -> {

            voiceChannel.getManager().putMemberPermissionOverride(member.getIdLong(), List.of(Permission.PRIORITY_SPEAKER, Permission.MANAGE_CHANNEL), List.of()).queue();

            voiceChannel.sendMessage(member.getAsMention())
                    .addEmbeds(new EmbedBuilder()
                            .setTitle("Voicechannel Controller")
                            .setDescription("Verwalte den Channel")
                            .setColor(Color.WHITE)
                            .build()).addActionRow(Button.secondary("jtc-change-title", "Titel ändern")).queue();

            JTCDto dto = new JTCDto();
            dto.setChannelId(voiceChannel.getId());
            dto.setCategoryId(category.getId());
            dto.setChannelOwnerId(member.getId());

            this.jtcRepo.save(dto);

            onChannelCreated.accept(voiceChannel);
        });
    }

}

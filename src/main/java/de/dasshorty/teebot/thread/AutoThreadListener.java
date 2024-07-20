package de.dasshorty.teebot.thread;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReference;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class AutoThreadListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        List<String> threadChannels = this.getThreadChannels();

        if (event.getMember() == null) {
            return;
        }

        User user = Objects.requireNonNull(event.getMember()).getUser();

        if (user.isBot() || user.isSystem()) {
            return;
        }

        if (!threadChannels.contains(event.getChannel().getId())) {
            return;
        }

        Message message = event.getMessage();

        if (message.getMessageReference() == null) {
            return;
        }

        MessageReference messageReference = message.getMessageReference();

        String contentDisplay = messageReference.getMessage().getContentDisplay();

        if (contentDisplay.length() > 10) {
            contentDisplay = contentDisplay.substring(0, 10) + "... ";
        }

        if (contentDisplay.isEmpty() || contentDisplay.isBlank()) {
            contentDisplay = "Thread von " + messageReference.getMessage().getMember().getEffectiveName();
        }

        messageReference.getMessage().createThreadChannel(contentDisplay).queue(threadChannel -> {
            threadChannel.addThreadMember(Objects.requireNonNull(messageReference.getMessage().getMember())).queue();
            threadChannel.addThreadMember(Objects.requireNonNull(message.getMember())).queue();

            threadChannel.sendMessage("Thread gestartet durch " + message.getMember().getAsMention() + " mit: *" + event.getMessage().getContentDisplay() + "*").queue();
            event.getMessage().delete().queue();
        });

    }

    private List<String> getThreadChannels() {

        String rawList = System.getenv("AUTO_THREAD_CHANNEL");

        String[] split = rawList.replaceAll("\\[", "").replaceAll("\\]", "").split(",");

        return Arrays.stream(split).toList();
    }
}

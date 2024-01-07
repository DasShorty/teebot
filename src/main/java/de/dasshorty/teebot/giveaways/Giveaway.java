package de.dasshorty.teebot.giveaways;

import com.google.gson.Gson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bson.Document;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public record Giveaway(long giveawayId, int winner, String description, long endTime, String messageId, boolean active,
                       ArrayList<String> enteredMemberIds) {

    @Contract("_ -> new")
    public @NotNull Giveaway setActive(boolean isActive) {
        return new Giveaway(this.giveawayId, this.winner, this.description, this.endTime, this.messageId, isActive, this.enteredMemberIds);
    }

    static Giveaway fromJson(String json) {
        return new Gson().fromJson(json, Giveaway.class);
    }

    long getEndTimeCalculated() {
        return Instant.now().getEpochSecond() + this.endTime;
    }

    void endGiveaway(Guild guild) {

        List<Member> chosenMembers = new ArrayList<>();

        Random random = new Random(new Random().nextLong());

        for (int i = 0; i < this.winner; i++) {


            String chosenMember;

            do {

                int chosenMemberId = random.nextInt(this.enteredMemberIds.size() - 1);

                chosenMember = this.enteredMemberIds.get(chosenMemberId);

            } while (chosenMembers.contains(chosenMember));

            chosenMembers.add(guild.getMemberById(chosenMember));

        }

        this.sendGiveawayEndMessage(guild, chosenMembers);
    }

    private void sendGiveawayEndMessage(Guild guild, List<? extends Member> winners) {

        TextChannel giveawayChannel = guild.getTextChannelById("1159846464990740642");

        assert giveawayChannel != null;
        Message giveawayMessage = giveawayChannel.getHistory().getMessageById(this.messageId);

        if (giveawayMessage != null)
            giveawayMessage.editMessageComponents(ActionRow.of(Button.secondary("no-id", "Giveaway ist bereits vorbei!").asDisabled())).queue();

        StringBuilder winnerMention = new StringBuilder();

        winners.forEach(member -> winnerMention.append(member.getAsMention()).append(" "));

        giveawayChannel.sendMessage(winnerMention.toString()).addEmbeds(new EmbedBuilder()
                .setAuthor("Giveaways")
                .setColor(Color.GREEN)
                .setDescription("Das Giveaway ist nun vorbei! Die Gewinner wurden ausgelöst.")
                .addField("Ausgelost am", "<t:" + Instant.now().getEpochSecond() + ":R>", false)
                .build()).queue();
    }

    public String getTimestamp() {
        return "<t:" + this.endTime + ":R>";
    }

    // time until giveaway ends in seconds
    public long getTimeUntilEnd() {
        long epochSecond = Instant.now().getEpochSecond();
        return this.endTime - epochSecond;
    }

    Document toDocument() {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this), Document.class);
    }

}

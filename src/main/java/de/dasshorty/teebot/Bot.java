package de.dasshorty.teebot;

import de.dasshorty.teebot.announcement.AnnouncementCommand;
import de.dasshorty.teebot.api.APIHandler;
import de.dasshorty.teebot.api.mongo.MongoHandler;
import de.dasshorty.teebot.embedcreator.EmbedCommand;
import de.dasshorty.teebot.embedcreator.EmbedDatabase;
import de.dasshorty.teebot.embedcreator.steps.step1.AuthorButton;
import de.dasshorty.teebot.embedcreator.steps.step1.AuthorModal;
import de.dasshorty.teebot.embedcreator.steps.step2.AddFieldButton;
import de.dasshorty.teebot.embedcreator.steps.step2.FieldButton;
import de.dasshorty.teebot.embedcreator.steps.step2.FieldModal;
import de.dasshorty.teebot.embedcreator.steps.step3.AddFooterButton;
import de.dasshorty.teebot.embedcreator.steps.step3.FooterButton;
import de.dasshorty.teebot.embedcreator.steps.step3.FooterModal;
import de.dasshorty.teebot.embedcreator.steps.step4.StyleButton;
import de.dasshorty.teebot.embedcreator.steps.step4.color.AddColorButton;
import de.dasshorty.teebot.embedcreator.steps.step4.color.ColorModal;
import de.dasshorty.teebot.embedcreator.steps.step4.image.AddImageButton;
import de.dasshorty.teebot.embedcreator.steps.step4.image.ImageModal;
import de.dasshorty.teebot.embedcreator.steps.step4.thumbnail.AddThumbnailButton;
import de.dasshorty.teebot.embedcreator.steps.step4.thumbnail.ThumbnailModal;
import de.dasshorty.teebot.embedcreator.steps.step4.timestamp.AddTimstampButton;
import de.dasshorty.teebot.embedcreator.steps.step5.FinishEmbedButton;
import de.dasshorty.teebot.membercounter.UpdateMemberCounter;
import de.dasshorty.teebot.selfroles.SelfRoleCommand;
import de.dasshorty.teebot.selfroles.SelfRoleDatabase;
import de.dasshorty.teebot.twitch.TwitchBot;
import de.dasshorty.teebot.twitch.TwitchCommand;
import de.dasshorty.teebot.twitch.TwitchDatabase;
import de.dasshorty.teebot.welcomeembed.SendWelcomeEmbed;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class Bot {
    @SneakyThrows
    public static void main(String[] args) {

        val builder = JDABuilder.createDefault(System.getenv("BOT_TOKEN"));

        val mongoHandler = new MongoHandler();

        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS);
        builder.enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.MEMBER_OVERRIDES);

        val api = new APIHandler(builder);

        builder.addEventListeners(new SendWelcomeEmbed());

        val jda = builder.build().awaitReady();

        val embedDatabase = new EmbedDatabase(mongoHandler);

        api.addSlashCommand(new EmbedCommand(embedDatabase));

        // Author
        api.addButton(new AuthorButton(embedDatabase));
        api.addModal(new AuthorModal(embedDatabase));

        // Fields
        api.addButton(new FieldButton(embedDatabase));
        api.addButton(new AddFieldButton());
        api.addModal(new FieldModal(embedDatabase));

        // Footer
        api.addButton(new FooterButton(embedDatabase));
        api.addButton(new AddFooterButton());
        api.addModal(new FooterModal(embedDatabase));

        // Style
        api.addButton(new StyleButton(embedDatabase));

        api.addButton(new AddThumbnailButton());
        api.addModal(new ThumbnailModal(embedDatabase));

        api.addButton(new AddImageButton());
        api.addModal(new ImageModal(embedDatabase));

        api.addButton(new AddTimstampButton(embedDatabase));

        api.addButton(new AddColorButton());
        api.addModal(new ColorModal(embedDatabase));

        api.addButton(new FinishEmbedButton(embedDatabase));

        val selfRoleDatabase = new SelfRoleDatabase(mongoHandler);
        api.addSlashCommand(new SelfRoleCommand(selfRoleDatabase, embedDatabase));

        api.addSlashCommand(new AnnouncementCommand(embedDatabase));

        val guild = jda.getGuilds().get(0);

        // twitch

        val twitchDatabase = new TwitchDatabase(mongoHandler);

        val twitchBot = new TwitchBot(twitchDatabase, guild);

        api.addSlashCommand(new TwitchCommand(twitchBot));

        new UpdateMemberCounter(guild);

        api.getCommandHandler().updateCommands(guild);
    }
}
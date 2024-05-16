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
import de.dasshorty.teebot.giveaways.EnterGiveawayButton;
import de.dasshorty.teebot.giveaways.GiveawayCommand;
import de.dasshorty.teebot.giveaways.GiveawayDatabase;
import de.dasshorty.teebot.giveaways.GiveawayManager;
import de.dasshorty.teebot.jtc.JTCDatabase;
import de.dasshorty.teebot.jtc.JTCVoiceListener;
import de.dasshorty.teebot.jtc.button.ChangeTitleButton;
import de.dasshorty.teebot.jtc.button.EnterNewTitleModal;
import de.dasshorty.teebot.membercounter.UpdateMemberCounter;
import de.dasshorty.teebot.notification.twitch.TwitchBot;
import de.dasshorty.teebot.notification.twitch.TwitchCommand;
import de.dasshorty.teebot.notification.twitch.TwitchDatabase;
import de.dasshorty.teebot.notification.youtube.YoutubeNotificationDatabase;
import de.dasshorty.teebot.notification.youtube.YoutubeNotificationManager;
import de.dasshorty.teebot.selfroles.SelfRoleCommand;
import de.dasshorty.teebot.selfroles.SelfRoleDatabase;
import de.dasshorty.teebot.tickets.TicketCommand;
import de.dasshorty.teebot.tickets.TicketDatabase;
import de.dasshorty.teebot.tickets.create.CreateTicketButton;
import de.dasshorty.teebot.tickets.create.DescriptionTicketModal;
import de.dasshorty.teebot.tickets.create.SelectTicketReasonMenu;
import de.dasshorty.teebot.tickets.listener.TicketMessageListener;
import de.dasshorty.teebot.tickets.management.TicketAddTeamButton;
import de.dasshorty.teebot.tickets.management.TicketClaimButton;
import de.dasshorty.teebot.tickets.management.TicketCloseButton;
import de.dasshorty.teebot.welcomeembed.SendWelcomeEmbed;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;

public class Bot {

    public static void main(String[] args) throws InterruptedException {

        OkHttpClient httpClient = new OkHttpClient();

        JDABuilder builder = JDABuilder.createDefault(System.getenv("BOT_TOKEN"));

        MongoHandler mongoHandler = new MongoHandler();

        builder.setMemberCachePolicy(MemberCachePolicy.ALL);

        builder.setActivity(Activity.customStatus("Trinkt Kaffee"));

        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES);
        builder.enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY, CacheFlag.EMOJI, CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);

        APIHandler api = new APIHandler(builder);

        JTCDatabase jtcDatabase = new JTCDatabase(mongoHandler);

        TicketDatabase ticketDatabase = new TicketDatabase(mongoHandler);

        builder.addEventListeners(new SendWelcomeEmbed(), new JTCVoiceListener(jtcDatabase), new TicketMessageListener(ticketDatabase));

        JDA jda = builder.setAutoReconnect(true).build().awaitReady();

        EmbedDatabase embedDatabase = new EmbedDatabase(mongoHandler);

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

        SelfRoleDatabase selfRoleDatabase = new SelfRoleDatabase(mongoHandler);
        api.addSlashCommand(new SelfRoleCommand(selfRoleDatabase, embedDatabase));

        api.addSlashCommand(new AnnouncementCommand(embedDatabase));

        Guild guild = jda.getGuilds().get(0);

        // Giveaway
        GiveawayDatabase giveawayDatabase = new GiveawayDatabase(mongoHandler);
        GiveawayManager giveawayManager = new GiveawayManager(giveawayDatabase, guild);
        api.addSlashCommand(new GiveawayCommand(giveawayDatabase, giveawayManager));
        api.addButton(new EnterGiveawayButton(giveawayDatabase));


        // Tickets
        api.addSlashCommand(new TicketCommand(embedDatabase, ticketDatabase));
        api.addButton(new CreateTicketButton());
        api.addStringMenu(new SelectTicketReasonMenu(ticketDatabase));
        api.addModal(new DescriptionTicketModal(ticketDatabase));

        api.addButton(new TicketClaimButton(ticketDatabase));
        api.addButton(new TicketCloseButton(ticketDatabase));
        api.addButton(new TicketAddTeamButton(ticketDatabase));

        // twitch

        TwitchDatabase twitchDatabase = new TwitchDatabase(mongoHandler);

        TwitchBot twitchBot = new TwitchBot(twitchDatabase, guild);

        api.addSlashCommand(new TwitchCommand(twitchBot));

        // youtube

        YoutubeNotificationDatabase youtubeDatabase = new YoutubeNotificationDatabase(mongoHandler);
        YoutubeNotificationManager youtubeManager = new YoutubeNotificationManager(youtubeDatabase, httpClient);

        youtubeManager.initCheck(guild);

        new UpdateMemberCounter(guild);

        api.getCommandHandler().updateCommands(guild);

        // JTC
        api.addButton(new ChangeTitleButton(jtcDatabase));
        api.addModal(new EnterNewTitleModal(jtcDatabase));

    }
}
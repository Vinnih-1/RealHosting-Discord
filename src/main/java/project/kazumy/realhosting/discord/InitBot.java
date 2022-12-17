package project.kazumy.realhosting.discord;

import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import project.kazumy.realhosting.discord.commands.manager.CommandManager;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.listener.EventListener;
import project.kazumy.realhosting.discord.listener.InteractionManager;
import project.kazumy.realhosting.discord.services.ServiceManager;
import project.kazumy.realhosting.discord.services.panel.PanelManager;
import project.kazumy.realhosting.discord.services.panel.ServerType;
import project.kazumy.realhosting.discord.services.payment.PaymentManager;
import project.kazumy.realhosting.discord.services.ticket.manager.TicketManager;

import java.util.logging.Logger;

public class InitBot {

    public static CommandManager commandManager = new CommandManager();
    public static InteractionManager interactionManager = new InteractionManager();
    public static PaymentManager paymentManager = new PaymentManager();
    public static PanelManager panelManager = new PanelManager();
    public static TicketManager ticketManager;
    public static Configuration config;
    public static JDA jda;

    private static InitBot instance;

    public InitBot(String token) {
        try {
            this.jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build().awaitReady();
        } catch(Exception e) {
            Logger.getGlobal().severe("The bot cannot be loaded! Exiting code...: " + e.getMessage());
            System.exit(1);
        }
        instance = this;
        jda.addEventListener(new EventListener(this));

        initConfig();
        val guild = jda.getGuildById(config.getString("bot.guild.id"));

        interactionManager.initInteraction();
        ServiceManager.loadServices(this, config);

        paymentManager.loadPlans();
        panelManager.expireServerTimer(guild, config);

        commandManager.loadSlashCommands();
        commandManager.loadPrefixCommands();
        commandManager.registerSlashCommand(guild);
    }

    public void initConfig() {
        config = new Configuration("configuration/config.yml")
                .buildIfNotExists()
                .addDefaults(config -> {
                    config.addDefault("bot.guild.ticket-category-id", "insert-your-category-id");
                    config.addDefault("bot.guild.ticket-chat-id", "insert-your-chat-id");
                    config.addDefault("bot.guild.id", "insert-your-guild-id");
                    config.addDefault("bot.guild.logs-chat-id", "insert-your-logs-chat-id");
                    config.addDefault("bot.payment.mercado-pago.access-token", "insert-your-access-token");
                });
    }

    public static InitBot getInstance() {
        return instance;
    }
}

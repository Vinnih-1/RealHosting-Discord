package project.kazumy.realhosting.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
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

    public static TicketManager ticketManager;
    public static PaymentManager paymentManager;
    public static PanelManager panelManager;

    public static Configuration config;
    public static JDA jda;

    public InitBot(String token) {
        try {
            this.jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS)
                    .build().awaitReady();
        } catch(Exception e) {
            Logger.getGlobal().severe("The bot cannot be loaded! Exiting code...: " + e.getMessage());
            System.exit(1);
        }

        jda.addEventListener(new EventListener(this));

        initConfig();
        interactionManager.initInteraction();
        ServiceManager.loadServices(this, config);

        paymentManager.loadPlans();

        commandManager.loadSlashCommands();
        commandManager.registerSlashCommand(jda.getGuildById(config.getString("bot.guild.id")));
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
}

package project.kazumy.realhosting.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import project.kazumy.realhosting.discord.commands.manager.CommandManager;
import project.kazumy.realhosting.discord.configuration.basic.GuildValue;
import project.kazumy.realhosting.discord.configuration.registry.ConfigurationRegistry;
import project.kazumy.realhosting.discord.listener.EventListener;
import project.kazumy.realhosting.discord.listener.InteractionManager;
import project.kazumy.realhosting.discord.services.ServiceManager;
import project.kazumy.realhosting.discord.services.panel.PanelManager;
import project.kazumy.realhosting.discord.services.payment.PaymentManager;
import project.kazumy.realhosting.discord.services.ticket.manager.TicketManager;

import java.util.logging.Logger;

public class InitBot {

    public static CommandManager commandManager = new CommandManager();
    public static InteractionManager interactionManager = new InteractionManager();
    public static PaymentManager paymentManager = new PaymentManager();
    public static PanelManager panelManager = new PanelManager();
    public static TicketManager ticketManager;
    public static JDA jda;

    private static InitBot instance;

    public InitBot(String token) {
        new ConfigurationRegistry().register();
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

        interactionManager.initInteraction();
        ServiceManager.loadServices(this);

        /*paymentManager.loadPlans();
        panelManager.expireServerTimer(jda.getGuildById(GuildValue.instance().id()));
        commandManager.loadSlashCommands();
        commandManager.loadPrefixCommands();
        commandManager.registerSlashCommand(jda.getGuildById(GuildValue.instance().id()));*/
    }

    public static InitBot getInstance() {
        return instance;
    }
}

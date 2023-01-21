package project.kazumy.realhosting.discord;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import project.kazumy.realhosting.configuration.registry.ConfigurationRegistry;
import project.kazumy.realhosting.discord.listener.EventListener;
import project.kazumy.realhosting.discord.services.ticket.manager.TicketManager;

import java.util.logging.Logger;

@Getter
public class DiscordMain {

    private TicketManager ticketManager;
    private JDA jda;

    public DiscordMain(String token) {
        new ConfigurationRegistry().register();
        try {
            this.jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .addEventListeners(new EventListener(this))
                    .build().awaitReady();
        } catch(Exception e) {
            Logger.getGlobal().severe("The bot cannot be loaded! Exiting code...: " + e.getMessage());
            System.exit(1);
        }
        this.ticketManager = TicketManager.of(this);

        /*paymentManager.loadPlans();
        panelManager.expireServerTimer(jda.getGuildById(GuildValue.instance().id()));
        commandManager.loadSlashCommands();
        commandManager.registerSlashCommand(jda.getGuildById(GuildValue.instance().id()));*/
    }
}

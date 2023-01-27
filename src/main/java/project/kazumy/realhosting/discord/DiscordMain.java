package project.kazumy.realhosting.discord;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import project.kazumy.realhosting.configuration.basic.GuildValue;
import project.kazumy.realhosting.discord.listener.EventListener;
import project.kazumy.realhosting.discord.services.ticket.manager.TicketManager;
import project.kazumy.realhosting.discord.services.ticket.repository.TicketRepository;
import project.kazumy.realhosting.model.entity.client.manager.ClientManager;
import project.kazumy.realhosting.model.panel.Panel;
import project.kazumy.realhosting.model.plan.PlanService;

import java.util.logging.Logger;

@Getter
@Data(staticConstructor = "of")
public class DiscordMain {

    private final SQLExecutor executor;
    private final ClientManager clientManager;
    private final PlanService planService;
    private final Panel panel;

    private TicketManager ticketManager;
    private JDA jda;

    public DiscordMain startup(String token) {
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
        this.ticketManager = TicketManager.of(jda.getGuildById(GuildValue.get(GuildValue::id)), TicketRepository.of(executor));
        return this;
    }
}

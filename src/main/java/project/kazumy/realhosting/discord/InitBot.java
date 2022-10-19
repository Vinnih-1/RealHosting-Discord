package project.kazumy.realhosting.discord;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;
import project.kazumy.realhosting.discord.commands.base.BaseSlashCommand;
import project.kazumy.realhosting.discord.commands.manager.CommandManager;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.listener.EventListener;
import project.kazumy.realhosting.discord.listener.InteractionManager;
import project.kazumy.realhosting.discord.services.payment.PaymentMP;
import project.kazumy.realhosting.discord.services.payment.PaymentManager;
import project.kazumy.realhosting.discord.services.ticket.manager.TicketManager;

import java.util.Objects;
import java.util.logging.Logger;

public class InitBot {

    public static final CommandManager commandManager = new CommandManager();
    public static final InteractionManager interactionManager = new InteractionManager();
    public static final TicketManager ticketManager = new TicketManager();
    public static final PaymentManager paymentManager = new PaymentManager();

    public static Configuration config;
    public JDA jda;

    @SneakyThrows
    public InitBot(String token) {
        this.jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS)
                .build().awaitReady();


        jda.addEventListener(new EventListener(this));

        initConfig();
        initCommand();
        ticketManager.loadService(jda, config).setOperating(true);
        interactionManager.initInteraction();
        PaymentMP.setAccessToken(InitBot.config.getString("bot.payment.mercado-pago.access-token"));
        new PaymentMP().waitPayment(paymentManager);
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

    public void initCommand() {
        if (this.jda == null) return;

        new Reflections("project.kazumy.realhosting.discord.commands").getSubTypesOf(BaseSlashCommand.class)
                .stream()
                .map(command -> {
                    try {
                        return command.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        Logger.getGlobal().severe("Some command could not be loaded: " + e.getMessage());
                        return null;
                    }
                }).filter(Objects::nonNull)
                .forEach(BaseSlashCommand::register);
    }
}

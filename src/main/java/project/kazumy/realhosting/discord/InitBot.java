package project.kazumy.realhosting.discord;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.reflections.Reflections;
import project.kazumy.realhosting.discord.commands.base.BaseCommand;
import project.kazumy.realhosting.discord.commands.manager.CommandManager;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.BaseService;

import java.util.Objects;

public class InitBot {

    public static final CommandManager commandManager = new CommandManager();

    public static Configuration config;
    public JDA jda;

    @SneakyThrows
    public InitBot(String token) {
        this.jda = JDABuilder.createDefault(token).build().awaitReady();

        initConfig();
        initCommand();

        new Reflections("project.kazumy.realhosting.discord.services").getSubTypesOf(BaseService.class)
                .stream()
                .map(service -> {
                    try {
                        return service.getDeclaredConstructor().newInstance().loadService(jda);
                    } catch (Exception e) {
                        System.err.println("Some service could not be loaded: " + e.getMessage());
                        return null;
                    }
                }).filter(Objects::nonNull)
                .forEach(service -> service.setOperating(true));
    }

    public void initConfig() {
        config = new Configuration("configuration/config.yml")
                .buildIfNotExists()
                .addDefaults(config -> {
                    config.addDefault("bot.guild.ticket-category-id", "insert-your-category-id");
                    config.addDefault("bot.guild.ticket-chat-id", "insert-your-chat-id");
                });
    }

    public void initCommand() {
        if (this.jda == null) return;

        new Reflections("project.kazumy.realhosting.discord.commands").getSubTypesOf(BaseCommand.class)
                .stream()
                .map(command -> {
                    try {
                        return command.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        System.err.println("Some command could not be loaded: " + e.getMessage());
                        return null;
                    }
                }).filter(Objects::nonNull)
                .forEach(BaseCommand::register);
    }
}

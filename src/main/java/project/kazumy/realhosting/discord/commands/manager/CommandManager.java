package project.kazumy.realhosting.discord.commands.manager;

import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.reflections.Reflections;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.commands.base.BasePrefixCommand;
import project.kazumy.realhosting.discord.commands.base.BaseSlashCommand;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Getter
public class CommandManager {

    private final Set<BaseSlashCommand> slashCommands = new HashSet<>();
    private final Set<BasePrefixCommand> prefixCommands = new HashSet<>();

    public void registerSlashCommand(Guild guild) {
        slashCommands.forEach(command -> {
            val commandData = new CommandDataImpl(command.getName(), command.getDescription());
            if (command.getOptions() != null) commandData.addOptions(command.getOptions());

            guild.upsertCommand(commandData).queue();
        });

    }

    public void loadSlashCommands() {
        new Reflections("project.kazumy.realhosting.discord.commands.slash").getSubTypesOf(BaseSlashCommand.class)
                .stream()
                .map(command -> {
                    try {
                        return command.newInstance();
                    } catch (Exception e) {
                        Logger.getGlobal().severe("Algum comando slash não pôde ser carregado: " + e.getMessage());
                        return null;
                    }
                }).forEach(slashCommands::add);
    }

    public void loadPrefixCommands(DiscordMain discordMain) {
        new Reflections("project.kazumy.realhosting.discord.commands.prefix").getSubTypesOf(BasePrefixCommand.class)
                .stream()
                .map(command -> {
                    try {
                        return command.getDeclaredConstructor(DiscordMain.class).newInstance(discordMain);
                    } catch (Exception e) {
                        Logger.getGlobal().severe("Algum comando prefix não pôde ser carregado: " + e.getMessage());
                        return null;
                    }
                }).forEach(prefixCommands::add);
    }
}

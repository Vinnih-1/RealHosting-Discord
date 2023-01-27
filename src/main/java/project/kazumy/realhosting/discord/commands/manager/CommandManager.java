package project.kazumy.realhosting.discord.commands.manager;

import lombok.Getter;
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
            guild.upsertCommand(new CommandDataImpl(command.getName(), command.getDescription())
                    .addOptions(command.getOptions())).queue();
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

    public void addSlashCommand(BaseSlashCommand command){
        this.slashCommands.add(command);
    }
}

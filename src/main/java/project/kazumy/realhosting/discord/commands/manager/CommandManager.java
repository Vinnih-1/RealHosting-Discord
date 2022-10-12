package project.kazumy.realhosting.discord.commands.manager;

import lombok.Getter;
import project.kazumy.realhosting.discord.commands.base.BaseSlashCommand;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CommandManager {

    private final Set<BaseSlashCommand> slashCommands = new HashSet<>();

    public void addSlashCommand(BaseSlashCommand command){
        this.slashCommands.add(command);
    }
}

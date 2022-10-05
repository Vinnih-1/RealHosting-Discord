package project.kazumy.realhosting.discord.commands.manager;

import lombok.Getter;
import project.kazumy.realhosting.discord.commands.base.BaseCommand;

import java.util.HashSet;
import java.util.Set;

@Getter
public class CommandManager {

    private final Set<BaseCommand> registeredCommands = new HashSet<>();

    public void addCommand(BaseCommand command){
        this.registeredCommands.add(command);
    }
}

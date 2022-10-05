package project.kazumy.realhosting.discord.commands.base;

import lombok.Data;
import net.dv8tion.jda.api.Permission;
import project.kazumy.realhosting.discord.InitBot;

@Data
public abstract class BaseCommand {

    public final String name, description;

    public final Permission permission;

    public void register() {
        InitBot.commandManager.addCommand(this);
    }
}

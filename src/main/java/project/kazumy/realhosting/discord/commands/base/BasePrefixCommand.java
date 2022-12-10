package project.kazumy.realhosting.discord.commands.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

@Getter
@AllArgsConstructor
public abstract class BasePrefixCommand {

    private final String name;

    private final Permission permission;

    public abstract void execute(Member member, Message message, String[] args);
}
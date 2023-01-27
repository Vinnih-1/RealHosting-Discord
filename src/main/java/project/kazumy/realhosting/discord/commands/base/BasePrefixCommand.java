package project.kazumy.realhosting.discord.commands.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import project.kazumy.realhosting.discord.DiscordMain;

@Getter
@AllArgsConstructor
public abstract class BasePrefixCommand {

    private final String name;

    private final Permission permission;

    private final DiscordMain discordMain;

    public abstract void execute(Member member, Message message, String[] args);
}
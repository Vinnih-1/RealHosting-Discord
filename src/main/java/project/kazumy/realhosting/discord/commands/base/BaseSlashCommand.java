package project.kazumy.realhosting.discord.commands.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import project.kazumy.realhosting.discord.DiscordMain;

import java.util.List;

@Data
@AllArgsConstructor
public abstract class BaseSlashCommand {

    public final String name, description;

    private final List<OptionData> options;

    private final DiscordMain discordMain;

    public abstract void execute(SlashCommandInteractionEvent event);
}

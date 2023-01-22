package project.kazumy.realhosting.discord.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import project.kazumy.realhosting.discord.DiscordMain;

@Data
@AllArgsConstructor
public abstract class InteractionService<T> {

    private final String id;
    private final DiscordMain discordMain;

    public abstract void execute(T event);
}

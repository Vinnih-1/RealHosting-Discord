package project.kazumy.realhosting.discord.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import project.kazumy.realhosting.discord.InitBot;

@Data
@AllArgsConstructor
public abstract class InteractionService<T> {

    private final String id;

    public abstract void execute(T event);

    public InitBot initBotInstance() {
        return InitBot.getInstance();
    }
}

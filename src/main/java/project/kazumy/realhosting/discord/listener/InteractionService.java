package project.kazumy.realhosting.discord.listener;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class InteractionService<T> {

    private final String id;

    public abstract void execute(T event);
}

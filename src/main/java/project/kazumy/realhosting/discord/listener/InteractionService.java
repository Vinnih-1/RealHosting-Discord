package project.kazumy.realhosting.discord.listener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public abstract class InteractionService<T> {

    private final String id;

    public abstract void execute(T event);

    public abstract void register(InteractionService<T> instance);
}

package project.kazumy.realhosting.discord.listener;

public interface InteractionService<T> {

    void execute(T event);

    void register(InteractionService<T> instance);
}

package project.kazumy.realhosting.discord.listener;

import lombok.Getter;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class InteractionManager {

    @Getter private final Set<InteractionService<?>> interactionList = new HashSet<>();

    public final void initInteraction() {
        new Reflections("project.kazumy.realhosting.discord.listener.interactions").getSubTypesOf(InteractionService.class)
                .stream()
                .map(interaction -> {
                    try {
                        return interaction.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        System.out.println("Some interaction could not be loaded! " + e.getMessage());
                        return null;
                    }
                }).filter(Objects::nonNull)
                .forEach(this::loadInteraction);
    }

    public final void loadInteraction(InteractionService<?> interaction) {
        this.interactionList.add(interaction);
    }

    public final void unloadInteraction(InteractionService<?> interaction) {
        this.interactionList.remove(interaction);
    }
}

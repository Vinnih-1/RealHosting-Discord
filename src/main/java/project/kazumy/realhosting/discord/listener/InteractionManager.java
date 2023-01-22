package project.kazumy.realhosting.discord.listener;

import lombok.Data;
import lombok.Getter;
import org.reflections.Reflections;
import project.kazumy.realhosting.discord.DiscordMain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data(staticConstructor = "of")
public class InteractionManager {

    @Getter private final Set<InteractionService<?>> interactionList = new HashSet<>();

    private final DiscordMain discordMain;

    public InteractionManager init() {
        new Reflections("project.kazumy.realhosting.discord.listener.interactions").getSubTypesOf(InteractionService.class)
                .stream()
                .map(interaction -> {
                    try {
                        return interaction.getDeclaredConstructor(DiscordMain.class).newInstance(discordMain);
                    } catch (Exception e) {
                        System.out.println("Some interaction could not be loaded! " + e.getMessage());
                        return null;
                    }
                }).filter(Objects::nonNull)
                .forEach(this::loadInteraction);

        return this;
    }

    public final void loadInteraction(InteractionService<?> interaction) {
        this.interactionList.add(interaction);
    }
}

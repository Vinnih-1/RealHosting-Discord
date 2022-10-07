package project.kazumy.realhosting.discord.listener;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.interactions.MenuInteraction;
import project.kazumy.realhosting.discord.services.ticket.Ticket;

public class EventListener extends ListenerAdapter {

    private final InitBot instance;
    public EventListener(InitBot instance) {
        this.instance = instance;
    }

    @Override
    public void onSelectMenuInteraction(@NotNull SelectMenuInteractionEvent event) {
        InitBot.interactionManager.getInteractionList()
                .stream()
                .filter(interaction -> interaction.getId().equals(event.getSelectMenu().getId()))
                .findAny()
                .ifPresentOrElse(interaction -> ((MenuInteraction)interaction).execute(event),
                        () -> System.out.println("Unloaded interaction id: " + event.getSelectMenu().getId()));
    }
}

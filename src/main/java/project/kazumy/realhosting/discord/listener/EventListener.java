package project.kazumy.realhosting.discord.listener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import project.kazumy.realhosting.discord.InitBot;

import java.util.logging.Logger;

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
                .ifPresentOrElse(interaction -> ((InteractionService<SelectMenuInteractionEvent>)interaction).execute(event),
                        () -> Logger.getGlobal().severe("Unloaded interaction id: " + event.getSelectMenu().getId()));
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        InitBot.interactionManager.getInteractionList()
                .stream()
                .filter(interaction -> interaction.getId().equals(event.getComponentId()))
                .findAny()
                .ifPresentOrElse(interaction -> ((InteractionService<ButtonInteractionEvent>)interaction).execute(event),
                        () -> Logger.getGlobal().severe("Unloaded interaction id: " + event.getComponentId()));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        InitBot.commandManager.getSlashCommands()
                .stream()
                .filter(command -> event.getName().equals(command.getName()))
                .findAny()
                .ifPresentOrElse(command -> command.execute(event),
                        () -> Logger.getGlobal().severe("Unloaded command id" + event.getName()));
    }
}

package project.kazumy.realhosting.discord.listener;

import lombok.val;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction;
import org.jetbrains.annotations.NotNull;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.commands.manager.CommandManager;

import java.util.Arrays;
import java.util.logging.Logger;

public class EventListener extends ListenerAdapter {

    private static final String PREFIX = "@";

    private final CommandManager commandManager = new CommandManager();
    private final InteractionManager interactionManager;

    private final DiscordMain discordMain;

    public EventListener(DiscordMain discordMain) {
        this.discordMain = discordMain;
        this.interactionManager = InteractionManager.of(discordMain).init();

        commandManager.loadPrefixCommands(discordMain);
        commandManager.loadSlashCommands();
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        interactionManager.getInteractionList()
                .stream()
                .filter(interaction -> interaction.getId().equals(event.getSelectMenu().getId()))
                .findAny()
                .ifPresentOrElse(interaction -> ((InteractionService<SelectMenuInteraction>)interaction).execute(event),
                        () -> {
                            Logger.getGlobal().severe("Unloaded interaction id: " + event.getSelectMenu().getId());
                        });
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        interactionManager.getInteractionList()
                .stream()
                .filter(interaction -> interaction.getId().equals(event.getModalId()))
                .findAny()
                .ifPresentOrElse(interaction -> ((InteractionService<ModalInteractionEvent>)interaction).execute(event),
                        () -> {
                            Logger.getGlobal().severe("Unloaded interaction id: " + event.getModalId());
                        });
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        interactionManager.getInteractionList()
                .stream()
                .filter(interaction -> event.getComponentId().contains(interaction.getId()))
                .findAny()
                .ifPresentOrElse(interaction -> ((InteractionService<ButtonInteractionEvent>)interaction).execute(event),
                        () -> Logger.getGlobal().severe("Unloaded interaction id: " + event.getComponentId()));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commandManager.getSlashCommands()
                .stream()
                .filter(command -> event.getName().equals(command.getName()))
                .findAny()
                .ifPresentOrElse(command -> command.execute(event),
                        () -> Logger.getGlobal().severe("Unloaded slash command id " + event.getName()));
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        val message = event.getMessage().getContentRaw();
        val commandMessage = message.split(" ")[0].replaceFirst(PREFIX, "");
        if (!message.startsWith(PREFIX)) return;
        commandManager.getPrefixCommands()
                .stream()
                .filter(command -> commandMessage.equals(command.getName()))
                .filter(command -> event.getMember().hasPermission(command.getPermission()))
                .findFirst()
                .ifPresentOrElse(command -> {
                    val args = Arrays.stream(message.replaceFirst(PREFIX, "").split(" "))
                            .skip(1).toArray(String[]::new);

                    event.getMessage().delete().queue();
                    command.execute(event.getMember(), event.getMessage(), args);
                }, () -> {
                    Logger.getGlobal().severe("Unloaded prefix command id " + commandMessage);
                });
    }
}

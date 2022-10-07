package project.kazumy.realhosting.discord.listener.interactions;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;

public class MenuInteraction extends InteractionService<SelectMenuInteractionEvent> {

    public MenuInteraction() {
        super("menu-ticket");
    }

    @Override
    public void execute(SelectMenuInteractionEvent event) {

    }

    @Override
    public void register(InteractionService<SelectMenuInteractionEvent> instance) {

    }
}

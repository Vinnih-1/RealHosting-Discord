package project.kazumy.realhosting.discord.listener.interactions;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import project.kazumy.realhosting.discord.listener.InteractionService;

public class PanelMenuInteraction extends InteractionService<SelectMenuInteractionEvent> {

    public PanelMenuInteraction() {
        super("panel-menu");
    }

    @Override
    public void execute(SelectMenuInteractionEvent event) {

    }
}

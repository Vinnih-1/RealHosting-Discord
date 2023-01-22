package project.kazumy.realhosting.discord.listener.interactions;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import project.kazumy.realhosting.discord.listener.InteractionService;

import java.awt.*;

public class PriceMenuInteraction extends InteractionService<SelectMenuInteractionEvent> {

    public PriceMenuInteraction() {
        super("price-menu");
    }

    @Override
    public void execute(SelectMenuInteractionEvent event) {
        event.deferReply(true).addEmbeds(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setDescription("Para adquirir um plano, abra um ticket na classe **'Adquirir Serviços'**")
                        .build()
        ).queue();
    }
}

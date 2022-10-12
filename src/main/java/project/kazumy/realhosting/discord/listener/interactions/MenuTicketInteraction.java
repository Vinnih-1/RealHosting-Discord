package project.kazumy.realhosting.discord.listener.interactions;

import lombok.val;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.ticket.Ticket;

import java.util.concurrent.TimeUnit;

public class MenuInteraction extends InteractionService<SelectMenuInteractionEvent> {

    public MenuInteraction() {
        super("menu-ticket");
    }

    @Override
    public void execute(SelectMenuInteractionEvent event) {
        if (event.getMember() == null) return;

        if (InitBot.ticketManager.hasOpenedTicket(event.getMember())) {
            event.deferReply(true).setContent(":x: Feche o ticket atual para abrir outro!").queue();
            return;
        }

        InitBot.ticketManager.openTicket(
                Ticket.builder()
                        .author(event.getMember())
                        .category(event.getSelectedOptions().get(0).getValue())
                        .id("test")
                        .build()
        );
        event.deferReply(true).setContent(":incoming_envelope: Seu ticket está aberto para o uso!").queue();
    }

    @Override
    public void register(InteractionService<SelectMenuInteractionEvent> instance) {

    }
}

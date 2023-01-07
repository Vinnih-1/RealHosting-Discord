package project.kazumy.realhosting.discord.listener.interactions;

import lombok.val;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.plan.PlanBuilder;
import project.kazumy.realhosting.model.panel.StageType;

public class CloseTicketButtonInteraction extends InteractionService<ButtonInteractionEvent> {

    public CloseTicketButtonInteraction() {
        super("close-ticket-button");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        if (event.getMember() == null) return;
        event.deferReply().setContent(":x: Este ticket foi fechado por: " + event.getUser().getAsTag()).queue();
        val ticketManager = this.initBotInstance().ticketManager;

        InitBot.paymentManager.getPlansByUserId(event.getUser().getId()).stream()
                                .filter(plan -> plan.getStageType() == StageType.CHOOSING_SERVER)
                                .forEach(PlanBuilder::deletePlan);

        if (ticketManager.hasOpenedTicketByChannelId(event.getChannel().getId()))
            ticketManager.getTicketByTextChannelId(event.getChannel().getId()).saveTicket();
        else event.getChannel().delete().queue();
    }
}

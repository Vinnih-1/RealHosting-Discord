package project.kazumy.realhosting.discord.listener.interactions;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.payment.plan.PlanBuilder;
import project.kazumy.realhosting.discord.services.payment.plan.StageType;

import java.awt.*;

public class CloseTicketButtonInteraction extends InteractionService<ButtonInteractionEvent> {

    public CloseTicketButtonInteraction() {
        super("close-ticket-button");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        if (event.getMember() == null) return;
        event.deferReply().addEmbeds(new EmbedBuilder()
                        .setTitle(":x: Este ticket foi fechado por: " + event.getUser().getAsTag())
                        .setFooter("Salvando o ticket, por favor aguarde...",
                                InitBot.config.getString("bot.guild.close-ticket.thumbnail"))
                        .setColor(Color.RED)
                .build()).queue();
        val channelManager = event.getChannel().asTextChannel().getManager();
        channelManager.setName("closed-" + event.getChannel().getName()).queue();

        event.getChannel().asTextChannel()
                .getMemberPermissionOverrides()
                .forEach(permission -> permission.delete().queue());

        InitBot.paymentManager.getPlansByUserId(event.getUser().getId()).stream()
                                .filter(plan -> plan.getStageType() == StageType.CHOOSING_SERVER)
                                .forEach(PlanBuilder::deletePlan);

        InitBot.ticketManager.getTicketByTextChannelId(event.getChannel().getId()).saveTicket();
    }
}

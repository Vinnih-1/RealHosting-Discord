package project.kazumy.realhosting.discord.listener.interactions;

import lombok.val;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import org.apache.commons.lang.RandomStringUtils;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.panel.ServerType;
import project.kazumy.realhosting.discord.services.payment.plan.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SelectServerMenuInteraction extends InteractionService<SelectMenuInteractionEvent> {

    public SelectServerMenuInteraction() {
        super("type-menu");
    }

    @Override
    public void execute(SelectMenuInteractionEvent event) {
        val plan = PlanBuilder.builder()
                .planData(PlanData.builder()
                        .userId(event.getUser().getId())
                        .planId(RandomStringUtils.randomAlphanumeric(15))
                        .userAsTag(event.getUser().getAsTag())
                        .build())
                .createDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .serverType(ServerType.valueOf(event.getSelectedOptions().get(0).getValue().split("\\.")[2].toUpperCase()))
                .stageType(StageType.CHOOSING_SERVER)
                .paymentIntent(PaymentIntent.CREATE_PLAN)
                .build().instanceConfig();

        val planData = plan.getPlanData();
        planData.setExternalReference(planData.getPlanId());
        plan.registerPlan();
        InitBot.paymentManager.getPlans().add(plan);
        event.getMessage().delete().queue(success -> InitBot.paymentManager.sendBuyMenu(event.getChannel().asTextChannel(), InitBot.config, "buy-menu"));
    }
}

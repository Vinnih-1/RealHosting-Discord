package project.kazumy.realhosting.discord.listener.interactions.menu;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.lang.RandomStringUtils;
import project.kazumy.realhosting.discord.DiscordMain;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.model.payment.intent.PaymentIntent;
import project.kazumy.realhosting.model.payment.utils.PaymentUtils;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class RenewPlanTicketMenuInteraction extends InteractionService<StringSelectInteractionEvent> {

    private final DiscordMain discordMain;

    public RenewPlanTicketMenuInteraction(DiscordMain discordMain) {
        super("renew-plan-menu", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(StringSelectInteractionEvent event) {
        val plan = discordMain.getPlanManager().getPlanById(event.getSelectedOptions().get(0).getValue());
        plan.setPrePlan(discordMain.getPlanManager().getPrePlanByType(
                discordMain.getPlanManager().getPrePlanTypeFromPlanById(plan.getId())));
        event.editSelectMenu(event.getSelectMenu().createCopy().setDefaultValues("").build()).queue();
        plan.setPaymentIntent(PaymentIntent.RENEW_PLAN);
        plan.setExternalId(RandomStringUtils.randomAlphanumeric(20));
        discordMain.getPlanManager().savePlan(plan);

        val qrData = discordMain.getPlanManager().purchase(plan, success -> {
            plan.setExpiration(plan.getExpiration().plusDays(30L));
            plan.setPaymentIntent(PaymentIntent.NONE);
            discordMain.getPlanManager().savePlan(plan);
            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("[STATUS DO PEDIDO DE RENOVAÇÃO]")
                            .setDescription(":white_check_mark: **|** Você renovou o seu plano por mais **30 dias**.")
                            .addField("Identificador", plan.getId(), true)
                            .addField("Expiração", plan.getExpiration().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true)
                    .build()).queue();
        });
        event.getChannel().sendMessage("Cobrança Automática de Serviços Prestados")
                .addEmbeds(new EmbedBuilder()
                        .setColor(Color.GRAY)
                        .setTitle(":white_check_mark: | PIX! Basta copiar e colar!")
                        .setDescription(qrData)
                        .build())
                .addFiles(FileUpload.fromData(PaymentUtils.getAsImage(event.getMember().getId(), qrData))).queue();
    }
}

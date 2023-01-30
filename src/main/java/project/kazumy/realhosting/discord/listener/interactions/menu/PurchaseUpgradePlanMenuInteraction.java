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

public class PurchaseUpgradePlanMenuInteraction extends InteractionService<StringSelectInteractionEvent> {

    private final DiscordMain discordMain;

    public PurchaseUpgradePlanMenuInteraction(DiscordMain discordMain) {
        super("purchase-upgrade-menu", discordMain);
        this.discordMain = discordMain;
    }

    @Override
    public void execute(StringSelectInteractionEvent event) {
        val planId = event.getMessage().getEmbeds().get(0).getFields().get(1).getValue();
        val plan = discordMain.getPlanManager().getPlanById(planId);
        plan.setPaymentIntent(PaymentIntent.UPGRADE_PLAN);
        plan.setExternalId(RandomStringUtils.randomAlphanumeric(20));
        plan.setPrePlan(discordMain.getPlanManager().getPrePlanByType(
                discordMain.getPlanManager().getPrePlanTypeFromPlanById(plan.getId())));
        discordMain.getPlanManager().savePlan(plan);
        val selected = discordMain.getPlanManager().getPrePlanById(event.getSelectedOptions().get(0)
                .getValue().split("\\.")[4].toUpperCase());

        if (plan.getPrePlan().getType().equals(selected.getType())) {
            event.deferReply().addEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription(":x: **|** Você não pode escolher o mesmo plano que já está ativo!")
                    .build()).queue();
            return;
        }

        if (plan.getPrePlan().getPrice().compareTo(selected.getPrice()) > 0) {
            event.deferReply().addEmbeds(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setDescription(":x: **|** Você não pode escolher um plano menor do que o atual!")
                    .build()).queue();
            return;
        }

        event.deferReply(true).addEmbeds(new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setDescription(String.format(":white_check_mark: **|** <@%s>, estamos processando o seu pedido.",
                        event.getMember().getId()))
                .build()).queue();

        val qrData = discordMain.getPlanManager().purchase(plan, success -> {
            plan.setPrePlan(selected);
            plan.setPaymentIntent(PaymentIntent.NONE);
            discordMain.getPlanManager().savePlan(plan);
            val client = discordMain.getClientManager().getClientById(plan.getOwner());
            client.getPanel().updateServerHardware(plan, server -> {
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setDescription(String.format(":white_check_mark: **|** Sucesso! Você aprimorou o seu " +
                                                "servidor para o plano **%s**!",
                                        plan.getPrePlan().getType()))
                        .build()).queue();
            });
        });
        event.getChannel().sendMessage("Cobrança Automática de Serviços Prestados")
                .addEmbeds(new EmbedBuilder()
                        .setColor(Color.GRAY)
                        .setTitle(":white_check_mark: | PIX! Basta copiar e colar!")
                        .setDescription(qrData)
                        .build())
                .addFiles(FileUpload.fromData(PaymentUtils.getAsImage(event.getMember().getId(), qrData))).queue();
        event.getMessage().delete().queue();
    }
}
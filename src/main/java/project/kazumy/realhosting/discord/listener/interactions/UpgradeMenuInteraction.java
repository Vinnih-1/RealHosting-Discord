package project.kazumy.realhosting.discord.listener.interactions;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.payment.plan.PaymentIntent;
import project.kazumy.realhosting.discord.services.payment.plan.PlanType;

import java.awt.*;
import java.math.BigDecimal;

public class UpgradeMenuInteraction extends InteractionService<SelectMenuInteractionEvent> {

    public UpgradeMenuInteraction() {
        super("upgrade-menu");
    }

    @Override
    public void execute(SelectMenuInteractionEvent event) {
        val logsChat = event.getJDA().getTextChannelById(InitBot.config.getString("bot.guild.logs-chat-id"));
        val section = InitBot.config.getConfigurationSection(event.getSelectedOptions().get(0).getValue());
        val price = event.getSelectedOptions().get(0).getLabel().split(":")[0]
                .replace("R$", "")
                .replace(",", ".")
                .replace(" ", "");
        val type = event.getSelectedOptions().get(0).getLabel().split("\\|")[1]
                .replace(" ", "");
        event.getMessage().getEmbeds().get(0).getFields().stream()
                .filter(field -> field.getName().equals("ID do Plano"))
                .map(field -> InitBot.paymentManager.getPlanById(field.getValue()))
                .findFirst().ifPresentOrElse(plan -> {
                    val paymentManager = InitBot.paymentManager;
                    val panelManager = InitBot.panelManager;
                    val config = InitBot.config;
                    val selectedPlanType = PlanType.valueOf(type);
                    if (plan.getPlanType() == selectedPlanType) {
                        event.deferReply().addEmbeds(new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setDescription("Você não pode escolher o mesmo plano que já está ativo!")
                                .build()).queue();
                        return;
                    }

                    if (plan.getPrice().compareTo(new BigDecimal(price)) > 0) {
                        event.deferReply().addEmbeds(new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setDescription("Você não pode escolher um plano menor do que o atual!")
                                .build()).queue();
                        return;
                    }
                    val qrCodeText = paymentManager.getPaymentQrCode(PaymentIntent.UPGRADE_PLAN, InitBot.config, plan);
                    val qrCodeImage = paymentManager.getPaymentQrCodeImage(qrCodeText, plan);


                }, () -> {
                    event.deferReply().addEmbeds(new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setDescription("Não consegui mais encontrar este plano!")
                            .build()).queue();
                });

    }
}

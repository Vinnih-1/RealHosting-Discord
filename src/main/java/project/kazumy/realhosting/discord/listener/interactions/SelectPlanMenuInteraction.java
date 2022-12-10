package project.kazumy.realhosting.discord.listener.interactions;

import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.panel.exceptions.PlanNotFoundException;
import project.kazumy.realhosting.discord.services.payment.plan.*;

import java.awt.*;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class SelectPlanMenuInteraction extends InteractionService<SelectMenuInteractionEvent> {

    public SelectPlanMenuInteraction() {
        super("buy-menu");
    }

    @Override
    @SneakyThrows
    public void execute(SelectMenuInteractionEvent event) {
        val logsChat = event.getJDA().getTextChannelById(InitBot.config.getString("bot.guild.logs-chat-id"));
        val section = InitBot.config.getConfigurationSection(event.getSelectedOptions().get(0).getValue());
        val price = event.getSelectedOptions().get(0).getLabel().split(":")[0]
                .replace("R$", "")
                .replace(",", ".")
                .replace(" ", "");
        val type = event.getSelectedOptions().get(0).getLabel().split("\\|")[1]
                .replace(" ", "");

        InitBot.paymentManager.getPlansByUserId(event.getUser().getId())
                .stream().filter(stage -> stage.getStageType() == StageType.CHOOSING_SERVER)
                .findFirst().ifPresentOrElse(plan -> {
                    val paymentManager = InitBot.paymentManager;
                    plan.getPlanData().setTitle(section.getString("name"));
                    plan.getPlanData().setDescription(section.getString("description"));
                    plan.getPlanData().setLogo(section.getString("logo"));
                    plan.getPlanData().setSkuId(section.getString("id"));
                    plan.getPlanData().setEmojiUnicode(Emoji.fromUnicode(section.getString("emoji")));
                    plan.setPrice(new BigDecimal(price));
                    plan.setPaymentIntent(PaymentIntent.CREATE_PLAN);
                    plan.setStageType(StageType.PENDING_PAYMENT);
                    plan.setPlanType(PlanType.valueOf(type.toUpperCase()));
                    plan.registerPlan();
                    val planData = plan.getPlanData();

                    event.deferReply(true).setContent(":repeat: Estamos produzindo seu QRCode, aguarde um momento.").queue();
                    event.getChannel().sendTyping().queue();

                    val qrCodeText = paymentManager.getPaymentQrCode(PaymentIntent.CREATE_PLAN, InitBot.config, plan);
                    val qrCodeImage = paymentManager.getPaymentQrCodeImage(qrCodeText, plan);

                    logsChat.sendMessage("QRCode PIX: " + event.getUser().getAsTag())
                            .addFiles(FileUpload.fromData(qrCodeImage)).queueAfter(2, TimeUnit.SECONDS, message -> {
                                val embed = new EmbedBuilder()
                                        .setAuthor(event.getSelectedOptions().get(0).getLabel(), planData.getLogo(), planData.getLogo())
                                        .setFooter("Auto atendimento da RealHosting", planData.getLogo())
                                        .setImage(message.getAttachments().get(0).getProxyUrl())
                                        .setColor(Color.GREEN)
                                        .build();

                                event.getChannel().sendMessage("RealHosting: Cobrança Automática de Serviços Prestados").addEmbeds(embed, new EmbedBuilder()
                                        .setTitle(":white_check_mark: PIX! Basta copiar e colar.")
                                        .setColor(Color.YELLOW)
                                        .setDescription(qrCodeText)
                                        .build(), new EmbedBuilder()
                                        .setTitle(":x: Vencimento do Pagamento")
                                        .setDescription("O tempo limite de utilização deste QRCode é de 10 minuto(s), este QRCode será apagado após o prazo de 10 minutos.")
                                        .setColor(Color.RED)
                                        .build()).queue(paymentMessage -> {
                                            event.getMessage().delete().queue();
                                    if (paymentMessage != null) paymentMessage.delete().queueAfter(10, TimeUnit.MINUTES);
                                    InitBot.paymentManager.getPaymentMP().detectCreatePayment(plan, onSuccess -> {
                                        if (event.getChannel() != null)
                                            InitBot.panelManager.emailMenu(InitBot.config, event.getChannel().asTextChannel());
                                        onSuccess.enablePlan(event.getGuild());
                                    });
                                });
                            });
                }, () -> event.deferReply().setContent(":x: " + new PlanNotFoundException().getMessage()).queue());
    }
}

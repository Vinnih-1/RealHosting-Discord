package project.kazumy.realhosting.discord.commands;

import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.commands.base.BaseSlashCommand;
import project.kazumy.realhosting.discord.services.payment.plan.PaymentIntent;
import project.kazumy.realhosting.discord.services.payment.plan.StageType;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class RenewPlanCommand extends BaseSlashCommand {

    public RenewPlanCommand() {
        super("renovar", "Renove seu plano para que ele não expire!",
                Arrays.asList(new OptionData(OptionType.STRING, "plano", "Indique qual plano você quer renovar.", true)));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        val typedPlan = event.getOption("plano").getAsString();

        event.getUser().openPrivateChannel().queue(channel -> {
            if (InitBot.paymentManager.hasPlanByPlanId(typedPlan)) {
                val plan = InitBot.paymentManager.getPlanById(typedPlan);
                val planData = plan.getPlanData();

                if (plan.getStageType() == StageType.PENDING_PAYMENT || plan.getStageType() == StageType.CHOOSING_SERVER) {
                    event.deferReply().setEmbeds(new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setDescription("Você não pode renovar um plano que ainda não está ativo!")
                            .build()).queue();
                    return;
                }
                event.deferReply(true).setContent(":white_check_mark: Estamos realizando todos os procedimentos, aguarde um pouco...").queue();
                event.getChannel().sendTyping().queue();
                val paymentManager = InitBot.paymentManager;
                val logsChat = event.getJDA().getTextChannelById(InitBot.config.getString("bot.guild.logs-chat-id"));
                val qrCodeText = paymentManager.getPaymentQrCode(PaymentIntent.RENEW_PLAN, InitBot.config, plan);
                val qrCodeImage = paymentManager.getPaymentQrCodeImage(qrCodeText, plan);
                logsChat.sendMessage("QRCode PIX: " + event.getUser().getAsTag()).addFiles(FileUpload.fromData(qrCodeImage))
                        .queue(qrcode -> {
                            val embed = new EmbedBuilder()
                                    .setAuthor(planData.getTitle(), planData.getLogo(), planData.getLogo())
                                    .setFooter("Auto atendimento da RealHosting", planData.getLogo())
                                    .setImage(qrcode.getAttachments().get(0).getProxyUrl())
                                    .setColor(Color.GREEN)
                                    .build();

                            channel.sendMessage("RealHosting: Cobrança Automática de Serviços Prestados").addEmbeds(embed, new EmbedBuilder()
                                    .setTitle(":white_check_mark: PIX! Basta copiar e colar.")
                                    .setColor(Color.YELLOW)
                                    .setDescription(qrCodeText)
                                    .build(), new EmbedBuilder()
                                    .setTitle(":x: Vencimento do Pagamento")
                                    .setDescription("O tempo limite de utilização deste QRCode é de 10 minuto(s), este QRCode será apagado após o prazo de 10 minutos.")
                                    .setColor(Color.RED)
                                    .build()).queue(paymentMessage -> {
                                if (paymentMessage != null) paymentMessage.delete().queueAfter(10, TimeUnit.MINUTES);
                            });
                        });
                InitBot.paymentManager.getPaymentMP().detectRenewPayment(onSuccess -> {
                    plan.enablePlan(event.getGuild());
                    plan.updatePaymentIntent(PaymentIntent.NONE);

                    if (event.getChannel() == null) return;

                    if (!InitBot.panelManager.serverExistsByPlanId(plan.getPlanData().getPlanId())) {
                        channel.sendMessageEmbeds(new EmbedBuilder()
                                        .setColor(Color.RED)
                                        .setDescription(String.format("Não foi possível encontrar o servidor do plano %s para renovação! Por favor, abra um ticket.",
                                                plan.getPlanData().getPlanId()))
                                .build()).queue();
                        return;
                    }
                    val server = InitBot.panelManager.getServerByPlanId(plan.getPlanData().getPlanId());
                    server.getController().unsuspend().executeAsync(success -> {
                        channel.sendMessageEmbeds(new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setDescription("Detectamos seu pagamento! Seu plano foi renovado por mais 1 mês.")
                                .build()).queue();
                    });

                });
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setDescription(String.format(":white_check_mark: Um QRCode foi enviado em sua DM, %s", event.getUser().getAsTag()))
                        .build()).queue();
                return;
            }
            event.deferReply(true).setContent(":x: Não encontrei nenhum plano com esse identificador!").queue();
        }, failure -> {
            event.deferReply(true).setContent(":x: Você precisa estar com o DM aberto para renovar o seu plano!").queue();
        });
    }
}

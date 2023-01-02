package project.kazumy.realhosting.discord.listener.interactions;

import com.mattmalec.pterodactyl4j.DataType;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.configuration.basic.GuildValue;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.plan.PaymentIntent;
import project.kazumy.realhosting.discord.services.plan.PlanType;

import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class UpgradeMenuInteraction extends InteractionService<SelectMenuInteractionEvent> {

    public UpgradeMenuInteraction() {
        super("upgrade-menu");
    }

    @Override
    public void execute(SelectMenuInteractionEvent event) {
        /*val logsChat = event.getJDA().getTextChannelById(GuildValue.get(GuildValue::logs));
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
                    event.deferReply(true).addEmbeds(new EmbedBuilder()
                            .setColor(Color.YELLOW)
                            .setDescription(":repeat: Aguarde um minuto, estamos gerando o seu QRCode.")
                            .build()).queue();

                    val planData = plan.getPlanData();
                    plan.setPrice(new BigDecimal(price).subtract(plan.getPrice()));
                    planData.setTitle(section.getString("name"));
                    planData.setDescription(section.getString("description"));
                    planData.setLogo(section.getString("logo"));
                    planData.setSkuId(section.getString("id"));
                    planData.setEmojiUnicode(Emoji.fromUnicode(section.getString("emoji")));
                    plan.setPlanType(selectedPlanType);

                    val qrCodeText = paymentManager.getPaymentQrCode(PaymentIntent.UPGRADE_PLAN, InitBot.config, plan);
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
                                    InitBot.paymentManager.getPaymentMP().detectUpgradePayment(plan.getPlanData().getPlanId(), onSuccess -> {
                                        plan.updatePaymentIntent(PaymentIntent.NONE);
                                        plan.updateNewInformation();

                                        val server = panelManager.getServerByPlanId(plan.getPlanData().getPlanId());
                                        server.getBuildManager().setCPU(selectedPlanType.getCpu())
                                                .setDisk(selectedPlanType.getDisk(), DataType.GB)
                                                .setMemory(selectedPlanType.getMemory(), DataType.GB)
                                                .setAllowedDatabases((int) selectedPlanType.getDatabase())
                                                .setAllowedBackups((int) selectedPlanType.getBackup())
                                                .executeAsync(success -> {
                                                    plan.setForcedApproved(false);
                                                    val member = event.getGuild().getMemberById(plan.getPlanData().getUserId());
                                                    val dateTime = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
                                                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                                    val externalReference = plan.getPlanData().getExternalReference();
                                                    val serverInfoEmbed = new EmbedBuilder();
                                                    serverInfoEmbed.setColor(Color.GREEN);
                                                    serverInfoEmbed.setAuthor(event.getUser().getAsTag(), "https://app.realhosting.com.br", plan.getPlanData().getLogo());
                                                    serverInfoEmbed.setTitle(plan.getPlanData().getTitle());
                                                    serverInfoEmbed.setFooter("RealHosting Bot às " + dateTime.format(formatter), plan.getPlanData().getLogo());
                                                    serverInfoEmbed.setThumbnail(member != null ? member.getUser().getAvatarUrl() : plan.getPlanData().getLogo());
                                                    serverInfoEmbed.addField("Autor", plan.getPlanData().getUserAsTag(), true);
                                                    serverInfoEmbed.addField("Plano", plan.getPlanType().toString(), true);
                                                    serverInfoEmbed.addField("ID do Plano", plan.getPlanData().getPlanId(), true);
                                                    serverInfoEmbed.addField("Data de Criação", plan.getCreateDate().format(formatter), true);
                                                    serverInfoEmbed.addField("Data de Pagamento", plan.getPaymentDate().format(formatter), true);
                                                    serverInfoEmbed.addField("Data de Expiração", plan.getExpirationDate().format(formatter), true);
                                                    serverInfoEmbed.addBlankField(false);
                                                    serverInfoEmbed.addField("Intenção de Pagamento", plan.getPaymentIntent().toString(), true);
                                                    serverInfoEmbed.addField("Referência Externa", externalReference == null ? "Nenhuma" : plan.getPlanData().getExternalReference(), true);

                                                    event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                                                            .setColor(Color.GREEN)
                                                            .setDescription("O seu servidor foi aprimorado com êxito!")
                                                            .build(),
                                                            serverInfoEmbed.build()).queue();
                                                });
                                    });
                                });
                            });
                }, () -> {
                    event.deferReply().addEmbeds(new EmbedBuilder()
                                    .setColor(Color.RED)
                                    .setDescription("Não consegui mais encontrar este plano!")
                            .build()).queue();
                });*/
    }
}

package project.kazumy.realhosting.discord.listener.interactions;

import io.nayuki.qrcodegen.QrCode;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.payment.PaymentMP;
import project.kazumy.realhosting.discord.services.payment.PaymentManager;
import project.kazumy.realhosting.discord.services.plan.Plan;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public class BuyMenuInteraction extends InteractionService<SelectMenuInteractionEvent> {

    public BuyMenuInteraction() {
        super("buy-menu");
    }

    @Override
    @SneakyThrows
    public void execute(SelectMenuInteractionEvent event) {
        val logsChat = event.getJDA().getTextChannelById(InitBot.config.getString("bot.guild.logs-chat-id"));
        val ticket = InitBot.ticketManager.getTicketByTextChannelId(event.getChannel().getId());
        val section = InitBot.config.getConfigurationSection(event.getSelectedOptions().get(0).getValue());
        val userId = InitBot.config.getString("bot.payment.mercado-pago.user-id");
        val posId = InitBot.config.getString("bot.payment.mercado-pago.external-pos-id");
        val accessToken = InitBot.config.getString("bot.payment.mercado-pago.access-token");
        val price = event.getSelectedOptions().get(0).getLabel().split(":")[0]
                .replace("R$", "")
                .replace(",", ".")
                .replace(" ", "");

        val plan = Plan.builder()
                .title(section.getString("name"))
                .description(section.getString("description"))
                .price(new BigDecimal(price))
                .logo(section.getString("logo"))
                .skuId(section.getString("id"))
                .emojiUnicode(Emoji.fromUnicode(section.getString("emoji")))
                .createDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .userId(event.getMember().getId())
                .planId(RandomStringUtils.randomAlphanumeric(15))
                .userAsTag(event.getUser().getAsTag())
                .enabled(false)
                .build().instanceConfig();

        event.deferReply(true).setContent(":repeat: Estamos produzindo seu QRCode, aguarde um momento.").queue();
        event.getChannel().sendTyping().queue();

        val payment = new PaymentMP();
        val request = payment.createRequestQrCode(plan, userId, posId, accessToken);
        val json = (JSONObject) new JSONParser().parse(request.getBody());

        System.out.println(request.getHeaders());
        System.out.println(request.getStatusCode());

        val qr = QrCode.encodeText(String.valueOf(json.get("qr_data")), QrCode.Ecc.MEDIUM);
        val img = PaymentManager.toImage(qr, 10, 4);
        val qrCodeImage = new File(ticket.getOpenedTicketFolder() + "/" + event.getChannel().getId() + ".png");
        ImageIO.write(img, "png", qrCodeImage);

        logsChat.sendMessage("QRCode PIX: " + event.getUser().getAsTag())
                .addFiles(FileUpload.fromData(qrCodeImage, "QRCode.png")).queueAfter(2, TimeUnit.SECONDS, message -> {
                    val embed = new EmbedBuilder()
                            .setAuthor(event.getSelectedOptions().get(0).getLabel(), plan.getLogo(), plan.getLogo())
                            .setFooter("Auto atendimento da RealHosting", plan.getLogo())
                            .setImage(message.getAttachments().get(0).getProxyUrl())
                            .setColor(Color.GREEN)
                            .build();

                    plan.registerPlan();
                    InitBot.paymentManager.getPlans().add(plan);

                    event.getChannel().sendMessage("RealHosting: Cobrança Automática de Serviços Prestados").addEmbeds(embed, new EmbedBuilder()
                            .setTitle(":white_check_mark: PIX! Basta copiar e colar.")
                            .setColor(Color.YELLOW)
                            .setDescription(String.valueOf(json.get("qr_data")))
                            .build(), new EmbedBuilder()
                                    .setTitle(":x: Vencimento do Pagamento")
                                    .setDescription("O tempo limite de utilização deste QRCode é de 10 minuto(s), este QRCode será apagado após o prazo de 10 minutos.")
                                    .setColor(Color.RED)
                            .build()).queue(paymentMessage -> {
                                if (paymentMessage != null) paymentMessage.delete().queueAfter(10, TimeUnit.MINUTES);
                    });
        });
    }
}

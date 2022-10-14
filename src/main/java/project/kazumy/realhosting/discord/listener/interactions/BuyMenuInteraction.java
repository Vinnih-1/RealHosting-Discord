package project.kazumy.realhosting.discord.listener.interactions;

import com.github.mattnicee7.pixqrcode.pixqrcode.PixQRCodeBuilder;
import io.nayuki.qrcodegen.QrCode;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.listener.InteractionService;
import project.kazumy.realhosting.discord.services.payment.PaymentManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;

public class BuyMenuInteraction extends InteractionService<SelectMenuInteractionEvent> {

    public BuyMenuInteraction() {
        super("buy-menu");
    }

    @Override
    @SneakyThrows
    public void execute(SelectMenuInteractionEvent event) {
        val logsChat = event.getJDA().getTextChannelById(InitBot.config.getString("bot.guild.logs-chat-id"));
        val logo = InitBot.config.getString("bot.guild.ticket.thumbnail");
        val ticket = InitBot.ticketManager.getTicketByTextChannelId(event.getChannel().getId());
        val price = event.getSelectedOptions().get(0).getLabel().split(":")[0]
                .replace("R$", "")
                .replace(",", ".")
                .replace(" ", "");

        if (logsChat == null) return;

        val pixQRCode = new PixQRCodeBuilder()
                .transactionIdentifier(event.getSelectedOptions().get(0).getValue())
                .receiverCity("Itaborai")
                .receiverFullName("RealHosting Hospedagem")
                .value(Double.parseDouble(price))
                .withValue(true)
                .pixKey("0a5f335c-f7e3-4270-b6cf-a294dad8feba")
                .build();
        val qr = QrCode.encodeText(pixQRCode.getAsText(), QrCode.Ecc.MEDIUM);
        val img = PaymentManager.toImage(qr, 10, 4);
        val qrCodeImage = new File(ticket.getOpenedTicketFolder() + "/" + ticket.getChannelId() + ".png");
        ImageIO.write(img, "png", qrCodeImage);

        logsChat.sendMessage("QRCode PIX: " + event.getUser().getAsTag())
                .addFiles(FileUpload.fromData(qrCodeImage, "QRCode.png")).queue(message -> {
                    val embed = new EmbedBuilder()
                            .setAuthor(event.getSelectedOptions().get(0).getLabel(), logo, logo)
                            .setFooter("Auto atendimento da RealHosting", logo)
                            .setImage(message.getAttachments().get(0).getProxyUrl())
                            .setColor(Color.GREEN)
                            .build();

                    event.deferReply().addEmbeds(embed, new EmbedBuilder()
                                    .setDescription(pixQRCode.getAsText())
                            .build()).queue();
                });
    }
}

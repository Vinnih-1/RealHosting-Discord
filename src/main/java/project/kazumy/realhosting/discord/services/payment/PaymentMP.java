package project.kazumy.realhosting.discord.services.payment;

import com.mattmalec.pterodactyl4j.ServerStatus;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.net.MPSearchRequest;
import com.sendgrid.Client;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import io.nayuki.qrcodegen.QrCode;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.services.panel.PanelManager;
import project.kazumy.realhosting.discord.services.payment.plan.PaymentIntent;
import project.kazumy.realhosting.discord.services.payment.plan.PlanBuilder;
import project.kazumy.realhosting.discord.services.payment.plan.StageType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.NoSuchElementException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class PaymentMP {

    private static final int SEARCH_LIMIT = 999;
    private static final int SEARCH_OFFSET = 0;
    private static final int DEFAULT_EXPIRATION = 10;

    @SneakyThrows
    public void setAccessToken(String accessToken) {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public void detectCreatePlanPayment(PaymentManager paymentManager, JDA jda) {
        val timer = new Timer();

        timer.schedule(new TimerTask() {

            @Override
            @SneakyThrows
            public void run() {
                val client = new PaymentClient();
                client.search(MPSearchRequest.builder().offset(SEARCH_OFFSET).limit(SEARCH_LIMIT).build())
                        .getResults().stream()
                        .filter(payment -> payment.getExternalReference() != null)
                        .filter(payment -> payment.getExternalReference().length() == 15)
                        .filter(payment -> paymentManager.hasPlanByPlanId(payment.getExternalReference()))
                        .filter(payment -> paymentManager.getPlanById(payment.getExternalReference()).getPaymentIntent() == PaymentIntent.CREATE_PLAN)
                        .forEach(payment -> {
                            Logger.getGlobal().info("Encontrado o pagamento do ticket id: " + payment.getExternalReference());
                            val plan = paymentManager.getPlanById(payment.getExternalReference());
                            plan.enablePlan();
                            plan.giveBuyerTag(jda, InitBot.config);
                            val guild = jda.getGuildById(InitBot.config.getString("bot.guild.id"));
                            val member = guild.getMemberById(plan.getPlanData().getUserId());

                            if (member == null) return;
                            if (!InitBot.ticketManager.hasOpenedTicket(member)) return;

                            val ticket = InitBot.ticketManager.getTicketByUserId(plan.getPlanData().getUserId());
                            val ticketChannel = ticket.getTicketChannel(jda);

                            if (ticketChannel != null) {
                                ticketChannel.sendMessageEmbeds(new EmbedBuilder()
                                                .setColor(Color.GREEN)
                                                .setAuthor("Verificação de Pagamento", "https://pannel.realhosting.com.br", plan.getPlanData().getLogo())
                                                .setDescription(":white_check_mark: Estamos verificando se está tudo correto e logo e em breve lhe daremos acesso ao seu plano.")
                                                .setFooter("Auto atendimento da RealHosting", plan.getPlanData().getLogo())
                                        .build()).queue();
                                InitBot.panelManager.emailMenu(InitBot.config, ticket, jda);
                            }
                        });
            }
        }, 0, 1000L * 10);
    }

    public void detectRenewPlanPayment(PaymentManager paymentManager, PanelManager panelManager) {
        val timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            @SneakyThrows
            public void run() {
                val client = new PaymentClient();
                client.search(MPSearchRequest.builder().offset(SEARCH_OFFSET).limit(SEARCH_LIMIT).build())
                        .getResults().stream()
                        .filter(payment -> payment.getExternalReference() != null)
                        .filter(payment -> payment.getExternalReference().length() == 8)
                        .filter(payment -> paymentManager.hasPlanByExternalReference(payment.getExternalReference()))
                        .map(payment -> paymentManager.getPlanByExternalReference(payment.getExternalReference()))
                        .forEach(plan -> {
                            plan.enablePlan();
                            plan.updatePaymentIntent(PaymentIntent.NONE);
                            val planId = plan.getPlanData().getPlanId();

                            if (!panelManager.serverExistsByPlanId(planId)) {
                                Logger.getGlobal().severe(String.format("Não foi possível encontrar o servidor do plano %s para renovação!", planId));
                                return;
                            }

                            try {
                                val server = panelManager.getServerByPlanId(planId);

                                if (server.getStatus() == ServerStatus.SUSPENDED) {
                                    server.getController().unsuspend().executeAsync(success -> {
                                        Logger.getGlobal().info(String.format("Encontramos o pagamento do plano %s e seu servidor foi reativado.", planId));
                                    });
                                } else Logger.getGlobal().info(String.format("Encontramos o pagamento do plano %s", planId));
                                if (plan.getPlanData().toUser(InitBot.jda.getGuildById("896553346738057226")) == null) return;
                                plan.getPlanData().toUser(InitBot.jda.getGuildById("896553346738057226")).openPrivateChannel()
                                    .queue(channel -> {
                                        channel.sendMessageEmbeds(new EmbedBuilder()
                                                        .setColor(Color.GREEN)
                                                        .setDescription("Detectamos seu pagamento! Seu plano foi renovado por mais 1 mês.")
                                                .build()).queue();
                                    });
                            } catch (NoSuchElementException e) {
                                Logger.getGlobal().severe(String.format("Não encontrei nenhum servidor com o id %s, por favor, crie um servidor com este ID", planId));
                                return;
                            }
                        });
            }
        }, 0, 1000L * 10);
    }

    @SneakyThrows
    public Response createRequestQrCode(PlanBuilder plan, String userId, String posId, String accessToken, String expiration) {
        val client = new Client();
        val request = new Request();
        val expirationDate = new StringBuilder();
        val planData = plan.getPlanData();

        if (expiration == null)
            expirationDate.append(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusMinutes(DEFAULT_EXPIRATION).toString().split("\\.")[0]);
        else
            expirationDate.append(expiration.split("\\.")[0]);

        expirationDate.append(".042-04:00");

        request.setMethod(Method.POST);
        request.setBaseUri("api.mercadopago.com");
        request.addHeader("Authorization", "Bearer " + accessToken);
        request.addHeader("Content-Type", "application/json");
        request.setBody("{\n" +
                "  \"external_reference\": \""+(planData.getExternalReference() != null ? planData.getExternalReference() : planData.getPlanId())+"\",\n" +
                "  \"title\": \" " + planData.getTitle() + " \",\n" +
                "  \"total_amount\": "+plan.getPrice().toString()+",\n" +
                "  \"description\": \""+planData.getDescription()+"\",\n" +
                "  \"expiration_date\": \""+expirationDate+"\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"sku_number\": \""+planData.getSkuId()+"\",\n" +
                "      \"category\": \"outros\",\n" +
                "      \"title\": \"" + planData.getTitle() + "\",\n" +
                "      \"description\": \""+planData.getDescription()+"\",\n" +
                "      \"unit_price\": "+plan.getPrice().toString()+",\n" +
                "      \"quantity\": 1,\n" +
                "      \"unit_measure\": \"unit\",\n" +
                "      \"total_amount\": "+plan.getPrice().toString()+"\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        request.setEndpoint(String.format("/instore/orders/qr/seller/collectors/%s/pos/%s/qrs", userId, posId));

        System.out.println(request.getBody());

        return client.api(request);
    }

    public Response createRequestQrCode(PlanBuilder plan, String userId, String posId, String accessToken) {
        return this.createRequestQrCode(plan, userId, posId, accessToken, null);
    }

    @SneakyThrows
    public File getAsImage(String qrCodeText, String userId) {

        val qr = QrCode.encodeText(qrCodeText, QrCode.Ecc.MEDIUM);
        val img = PaymentManager.toImage(qr, 10, 4);
        val qrCodeImage = new File( userId + ".png");
        ImageIO.write(img, "png", qrCodeImage);

        return qrCodeImage;
    }

    @SneakyThrows
    public String getAsText(Response response) {
        val json = (JSONObject) new JSONParser().parse(response.getBody());
        return String.valueOf(json.get("qr_data"));
    }

    public static File getQrFolder() {
        return new File("services/payment/qrs");
    }
}

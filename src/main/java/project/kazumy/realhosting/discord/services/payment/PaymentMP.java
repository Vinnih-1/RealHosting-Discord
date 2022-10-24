package project.kazumy.realhosting.discord.services.payment;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.net.MPSearchRequest;
import com.sendgrid.Client;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.services.payment.plan.PlanBuilder;
import project.kazumy.realhosting.discord.services.payment.plan.StageType;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class PaymentMP {

    private static final int SEARCH_LIMIT = 500;
    private static final int SEARCH_OFFSET = 0;
    private static final int DEFAULT_EXPIRATION = 10;

    @SneakyThrows
    public static void setAccessToken(String accessToken) {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public void waitPayment(PaymentManager paymentManager, JDA jda) {
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
                        .filter(payment -> !paymentManager.getPlanById(payment.getExternalReference()).isEnabled())
                        .forEach(payment -> {
                            Logger.getGlobal().info("Encontrado o pagamento do ticket id: " + payment.getExternalReference());
                            val plan = paymentManager.getPlanById(payment.getExternalReference());
                            plan.setPaymentDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
                            plan.setExpirationDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusDays(30L));
                            plan.setStageType(StageType.PAYED);
                            plan.validatePlan();

                            val ticket = InitBot.ticketManager.getTicketByUserId(plan.getUserId());
                            val ticketChannel = ticket.getTicketChannel(jda);


                            if (ticketChannel != null) {
                                ticketChannel.sendMessageEmbeds(new EmbedBuilder()
                                                .setColor(Color.GREEN)
                                                .setAuthor("Verificação de Pagamento", "https://painel.realhosting.com.br", plan.getLogo())
                                                .setDescription(":white_check_mark: Estamos verificando se está tudo correto e logo e em breve lhe daremos acesso ao seu plano.")
                                                .setFooter("Auto atendimento da RealHosting", plan.getLogo())
                                        .build()).queue();
                                InitBot.panelManager.emailMenu(InitBot.config, ticket, jda);
                            }

                        });
            }
        }, 0, 10000);
    }

    @SneakyThrows
    public Response createRequestQrCode(PlanBuilder plan, String userId, String posId, String accessToken, String expiration) {
        val client = new Client();
        val request = new Request();
        val expirationDate = new StringBuilder();

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
                "  \"external_reference\": \""+plan.getPlanId()+"\",\n" +
                "  \"title\": \" " + plan.getTitle() + " \",\n" +
                "  \"total_amount\": "+plan.getPrice().toString()+",\n" +
                "  \"description\": \""+plan.getDescription()+"\",\n" +
                "  \"expiration_date\": \""+expirationDate+"\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"sku_number\": \""+plan.getSkuId()+"\",\n" +
                "      \"category\": \"outros\",\n" +
                "      \"title\": \"" + plan.getTitle() + "\",\n" +
                "      \"description\": \""+plan.getDescription()+"\",\n" +
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
}

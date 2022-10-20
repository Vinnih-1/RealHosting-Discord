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
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.services.plan.PlanBuilder;
import project.kazumy.realhosting.discord.services.ticket.Ticket;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Timer;
import java.util.TimerTask;

public class PaymentMP {

    private static final int SEARCH_LIMIT = 50;
    private static final int SEARCH_OFFSET = 0;
    private static final int DEFAULT_EXPIRATION = 10;

    @SneakyThrows
    public static void setAccessToken(String accessToken) {
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    public void waitPayment(PaymentManager paymentManager) {
        val timer = new Timer();

        timer.schedule(new TimerTask() {

            @Override
            @SneakyThrows
            public void run() {
                val client = new PaymentClient();
                client.search(MPSearchRequest.builder().offset(SEARCH_OFFSET).limit(SEARCH_LIMIT).build())
                        .getResults().stream()
                        .filter(payment -> payment.getExternalReference() != null)
                        .filter(payment -> InitBot.ticketManager.hasOpenedTicket(payment.getExternalReference()))
                        .filter(payment -> payment.getExternalReference().equals(InitBot.ticketManager.getTicketById(payment.getExternalReference()).getId()))
                        .findFirst().ifPresent(payment -> {
                            System.out.println("encontrado o pagamento do ticket id: " + payment.getExternalReference());
                        });

            }
        }, 0, 10000);
    }

    @SneakyThrows
    public Response createRequestQrCode(PlanBuilder plan, Ticket ticket, String userId, String posId, String accessToken, String expiration) {
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
                "  \"external_reference\": \""+ticket.getId()+"\",\n" +
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

    public Response createRequestQrCode(PlanBuilder plan, Ticket ticket, String userId, String posId, String accessToken) {
        return this.createRequestQrCode(plan, ticket, userId, posId, accessToken, null);
    }
}

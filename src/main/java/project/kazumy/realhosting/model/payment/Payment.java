package project.kazumy.realhosting.model.payment;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.net.MPSearchRequest;
import com.sendgrid.Client;
import com.sendgrid.Method;
import com.sendgrid.Request;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import project.kazumy.realhosting.discord.configuration.basic.PaymentValue;
import project.kazumy.realhosting.model.plan.Plan;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * @author Vinícius Albert
 */
@Getter
public class Payment {

    private static final Integer SEARCH_OFFSET = 0;
    private static final Integer SEARCH_LIMIT = 900;
    private Plan plan;
    private String qrData;

    /**
     * Envia um request para a API do Mercado Pago, enviando todos os
     * dados sobre o plano, com a intenção de receber um QRCode
     * da conta do proprietário especificado na 'config.yml'
     * contendo os dados do plano que está sendo criado como preço,
     * título, descrição, etc...
     *
     * @param plan o plano que está sendo adquirido.
     * @return uma instância da classe Payment.
     */
    @SneakyThrows
    public Payment request(Plan plan) {
        this.plan = plan;
        MercadoPagoConfig.setAccessToken(PaymentValue.get(PaymentValue::accessToken));
        val client = new Client();
        val request = new Request();

        request.setMethod(Method.POST);
        request.setBaseUri("api.mercadopago.com");
        request.addHeader("Authorization", "Bearer " + PaymentValue.get(PaymentValue::accessToken));
        request.addHeader("Content-Type", "application/json");
        request.setBody("{\n" +
                "  \"external_reference\": \"" + plan.getId() + "\",\n" +
                "  \"title\": \" " + plan.getPrePlan().getTitle() + " \",\n" +
                "  \"total_amount\": " + plan.getPrePlan().getPrice().toString() + ",\n" +
                "  \"description\": \"" + plan.getPrePlan().getDescription() + "\",\n" +
                "  \"expiration_date\": \"" + LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).toString().split("\\.")[0] + ".042-04:00" + "\",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"sku_number\": \"" + plan.getPrePlan().getId() + "\",\n" +
                "      \"category\": \"outros\",\n" +
                "      \"title\": \"" + plan.getPrePlan().getTitle() + "\",\n" +
                "      \"description\": \"" + plan.getPrePlan().getDescription() + "\",\n" +
                "      \"unit_price\": " + plan.getPrePlan().getPrice().toString() + ",\n" +
                "      \"quantity\": 1,\n" +
                "      \"unit_measure\": \"unit\",\n" +
                "      \"total_amount\": " + plan.getPrePlan().getPrice().toString() + "\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        request.setEndpoint(String.format("/instore/orders/qr/seller/collectors/%s/pos/%s/qrs", PaymentValue.get(PaymentValue::userId), PaymentValue.get(PaymentValue::posId)));
        System.out.println(request.getBody());
        val response = client.api(request);
        this.qrData = String.valueOf(((JSONObject) new JSONParser().parse(response.getBody())).get("qr_data"));
        return this;
    }

    /**
     * Espera em até 30 minutos que o pagamento seja efetuado,
     * para a detecção e posteriormente ativação do plano adquirido.
     *
     * @param success responsável por continuar os procedimentos após o pagamento.
     */
    public void wait(Consumer<Plan> success) {
        val timer = new Timer();
        val limit = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusMinutes(30L);
        timer.schedule(new TimerTask() {
            @Override
            @SneakyThrows
            public void run() {
                if (limit.isBefore(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))) this.cancel();
                val client = new PaymentClient();
                val filters = (Map) new HashMap<String, Object>();
                filters.put("external_reference", plan.getId());
                val search = MPSearchRequest.builder().offset(SEARCH_OFFSET).limit(SEARCH_LIMIT).filters(filters).build();
                val result = client.search(search);
                result.getResults().stream()
                        .filter(payment -> payment.getExternalReference().equals(plan.getId()))
                        .findFirst().ifPresent(payment -> {
                            success.accept(plan);
                            this.cancel();
                        });
            }
        }, 0L, 1000L * 10L);
    }
}

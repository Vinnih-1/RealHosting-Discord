package project.kazumy.realhosting.model.payment;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.net.MPSearchRequest;
import com.sendgrid.Client;
import com.sendgrid.Method;
import com.sendgrid.Request;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import project.kazumy.realhosting.configuration.basic.PaymentValue;
import project.kazumy.realhosting.model.entity.client.manager.ClientManager;
import project.kazumy.realhosting.model.payment.coupon.repository.CouponRepository;
import project.kazumy.realhosting.model.payment.intent.PaymentIntent;
import project.kazumy.realhosting.model.plan.Plan;
import project.kazumy.realhosting.model.plan.manager.PlanManager;

import java.math.BigDecimal;
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
@Data(staticConstructor = "of")
public class Payment {

    private static final Integer SEARCH_OFFSET = 0;
    private static final Integer SEARCH_LIMIT = 900;
    private final ClientManager clientManager;
    private final PlanManager planManager;

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
    public String request(Plan plan) {
        MercadoPagoConfig.setAccessToken(PaymentValue.get(PaymentValue::accessToken));
        val paymentClient = new Client();
        val request = new Request();

        request.setMethod(Method.POST);
        request.setBaseUri("api.mercadopago.com");
        request.addHeader("Authorization", "Bearer " + PaymentValue.get(PaymentValue::accessToken));
        request.addHeader("Content-Type", "application/json");
        request.setBody("{\n" +
                "  \"external_reference\": \"" + plan.getExternalId() + "\",\n" +
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
        val response = paymentClient.api(request);
        return String.valueOf(((JSONObject) new JSONParser().parse(response.getBody())).get("qr_data"));
    }

    public void checkCoupon(Plan plan, CouponRepository repository) {
        if (plan.getCoupon().isEmpty()) return;
        val coupon = repository.findCouponByName(plan.getCoupon());
        if (coupon == null) return;
        if (LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).isAfter(coupon.getExpirateAt())) return;
        val discount = (coupon.getPercentage() * plan.getPrePlan().getPrice().doubleValue()) / 100;
        plan.getPrePlan().setPrice(new BigDecimal(plan.getPrePlan().getPrice().doubleValue() - discount));
    }

    /**
     * Espera em até 30 minutos que o pagamento seja efetuado,
     * para a detecção e posteriormente ativação do plano adquirido.
     *
     * @param success responsável por continuar os procedimentos após o pagamento.
     */
    public void wait(String planId, Consumer<Void> success) {
        val timer = new Timer();
        val limit = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusMinutes(30L);
        timer.schedule(new TimerTask() {
            @Override
            @SneakyThrows
            public void run() {
                if (limit.isBefore(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))) this.cancel();
                val plan = planManager.getPlanById(planId);
                System.out.println(plan.getPaymentIntent());
                if (plan.getPaymentIntent() == PaymentIntent.FORCE_APPROVAL) {
                    success.accept(null);
                    this.cancel();
                    return;
                }
                val paymentClient = new PaymentClient();
                val filters = (Map) new HashMap<String, Object>();
                filters.put("external_reference", plan.getExternalId());
                val search = MPSearchRequest.builder().offset(SEARCH_OFFSET).limit(SEARCH_LIMIT).filters(filters).build();
                val result = paymentClient.search(search);
                result.getResults().stream()
                        .filter(payment -> payment.getExternalReference().equals(plan.getExternalId()))
                        .findFirst().ifPresent(payment -> {
                            success.accept(null);
                            this.cancel();
                        });
            }
        }, 0L, 1000L * 10L);
    }
}
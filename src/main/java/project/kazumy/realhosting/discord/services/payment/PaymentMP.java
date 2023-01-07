package project.kazumy.realhosting.discord.services.payment;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.net.MPSearchRequest;
import com.sendgrid.Client;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import io.nayuki.qrcodegen.QrCode;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import project.kazumy.realhosting.discord.services.panel.PanelManager;
import project.kazumy.realhosting.model.payment.PaymentIntent;
import project.kazumy.realhosting.discord.services.plan.PlanBuilder;

import javax.imageio.ImageIO;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Data(staticConstructor = "of")
public class PaymentMP {

    private final PaymentManager paymentManager;
    private final PanelManager panelManager;
    private final JDA jda;

    private static final int SEARCH_LIMIT = 900;
    private static final int SEARCH_OFFSET = 0;
    private static final int DEFAULT_EXPIRATION = 10;

    @SneakyThrows
    public void setAccessToken(String accessToken) {
        MercadoPagoConfig.setAccessToken(accessToken);
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

    public void detectCreatePayment(PlanBuilder plan, Consumer<PlanBuilder> onSuccess) {
        val expiration = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusMinutes(DEFAULT_EXPIRATION);
        val client = new PaymentClient();

        new Timer()
                .schedule(new TimerTask() {
                    @Override
                    @SneakyThrows
                    public void run() {
                        if (expiration.isBefore(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))) {
                            Logger.getGlobal().info("O plano %s expirou após 10 minutos sem a detecção do pagamento");
                            this.cancel();
                            return;
                        }
                        val forcedPlan = paymentManager.getPlanById(plan.getPlanData().getPlanId());

                        if (forcedPlan.isForcedApproved()) {
                            onSuccess.accept(forcedPlan);
                            this.cancel();
                        }
                        client.search(MPSearchRequest.builder().offset(SEARCH_OFFSET).limit(SEARCH_LIMIT).build())
                                .getResults().stream()
                                .filter(payment -> payment.getExternalReference() != null)
                                .filter(payment -> payment.getExternalReference().length() == 15)
                                .filter(payment -> paymentManager.hasPlanByPlanId(payment.getExternalReference()))
                                .filter(payment -> paymentManager.getPlanById(payment.getExternalReference()).getPaymentIntent() == PaymentIntent.CREATE_PLAN)
                                .forEach(payment -> {
                                    onSuccess.accept(plan);
                                    this.cancel();
                                });
                    }
                }, 0L, 1000L);
    }

    public void detectRenewPayment(String planId, Consumer<PlanBuilder> onSuccess) {
        val expiration = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusMinutes(DEFAULT_EXPIRATION);
        val client = new PaymentClient();

        new Timer()
                .schedule(new TimerTask() {
                    @Override
                    @SneakyThrows
                    public void run() {
                        if (expiration.isBefore(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))) {
                            Logger.getGlobal().info("O plano %s expirou após 10 minutos sem a detecção do pagamento");
                            this.cancel();
                            return;
                        }
                        val forcedPlan = paymentManager.getPlanById(planId);

                        if (forcedPlan.isForcedApproved()) {
                            onSuccess.accept(forcedPlan);
                            this.cancel();
                        }
                        client.search(MPSearchRequest.builder().offset(SEARCH_OFFSET).limit(SEARCH_LIMIT).build())
                                .getResults().stream()
                                .filter(payment -> payment.getExternalReference() != null)
                                .filter(payment -> payment.getExternalReference().length() == 8)
                                .filter(payment -> paymentManager.hasPlanByExternalReference(payment.getExternalReference()))
                                .map(payment -> {
                                    val plan = paymentManager.getPlanByExternalReference(payment.getExternalReference());
                                    payment.getAdditionalInfo().getItems().get(0).getTitle();
                                    return plan;
                                })
                                .forEach(plan -> {
                                    onSuccess.accept(plan);
                                    this.cancel();
                                });
                    }
                }, 0L, 1000L);
    }

    public void detectUpgradePayment(String planId, Consumer<PlanBuilder> onSuccess) {
        val expiration = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).plusMinutes(DEFAULT_EXPIRATION);
        val client = new PaymentClient();

        new Timer()
                .schedule(new TimerTask() {
                    @Override
                    @SneakyThrows
                    public void run() {
                        if (expiration.isBefore(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))) {
                            Logger.getGlobal().info("O plano %s expirou após 10 minutos sem a detecção do pagamento");
                            this.cancel();
                            return;
                        }
                        val forcedPlan = paymentManager.getPlanById(planId);

                        if (forcedPlan.isForcedApproved()) {
                            onSuccess.accept(forcedPlan);
                            this.cancel();
                        }
                        client.search(MPSearchRequest.builder().offset(SEARCH_OFFSET).limit(SEARCH_LIMIT).build())
                                .getResults().stream()
                                .filter(payment -> payment.getExternalReference() != null)
                                .filter(payment -> payment.getExternalReference().length() == 20)
                                .filter(payment -> paymentManager.hasPlanByExternalReference(payment.getExternalReference()))
                                .map(payment -> paymentManager.getPlanByExternalReference(payment.getExternalReference()))
                                .forEach(plan -> {
                                    onSuccess.accept(plan);
                                    this.cancel();
                                });
                    }
                }, 0L, 1000L);
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

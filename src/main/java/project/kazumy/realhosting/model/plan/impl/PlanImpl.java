package project.kazumy.realhosting.model.plan.impl;

import lombok.*;
import project.kazumy.realhosting.model.entity.client.Client;
import project.kazumy.realhosting.model.panel.Panel;
import project.kazumy.realhosting.model.panel.ServerType;
import project.kazumy.realhosting.model.payment.intent.PaymentIntent;
import project.kazumy.realhosting.model.plan.Plan;
import project.kazumy.realhosting.model.panel.StageType;
import project.kazumy.realhosting.model.plan.pre.PrePlan;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * @author Vinícius Albert
 */
@Getter
@Builder
@ToString
public class PlanImpl extends Panel implements Plan {

    private final LocalDateTime creation, payment, expiration;
    @Setter private PaymentIntent paymentIntent;
    @Setter private StageType stageType;
    private final ServerType serverType;
    @Setter private PrePlan prePlan;
    private final String id;

    /**
     *
     * Obtém todos os dados necessários novo plano
     * para salvar no arquivo de dados do cliente
     * localizado em 'services/payment/client/(client.id).yml'.
     *
     * @param client instância do cliente que está efetuando o pagamento;
     */
    public void savePlan(Client client) {
        savePlan(client, null);
    }

    /**
     *
     * Obtém todos os dados necessários novo plano
     * para salvar no arquivo de dados do cliente
     * localizado em 'services/payment/client/(client.id).yml'.
     *
     * @param client instância do cliente que está efetuando o pagamento.
     * @param success callback para quando os dados terminarem de ser salvos.
     */
    @Override
    @SneakyThrows
    public void savePlan(Client client, Consumer<Plan> success) {
        val section = "client.plans." + this.getId();
        client.getConfig().set(section + ".type", this.getPrePlan().getType());
        client.getConfig().addDefault(section + ".server", this.serverType.toString());
        client.getConfig().set(section + ".stage", this.stageType.toString());
        client.getConfig().set(section + ".intent", this.paymentIntent.toString());
        client.getConfig().addDefault(section + ".creation", this.creation.toString());
        client.getConfig().addDefault(section + ".payment", this.payment.toString());
        client.getConfig().set(section + ".expiration", this.expiration.toString());
        client.getConfig().save();
        if (success != null) success.accept(this);
    }
}

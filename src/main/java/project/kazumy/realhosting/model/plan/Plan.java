package project.kazumy.realhosting.model.plan;

import project.kazumy.realhosting.model.entity.client.Client;
import project.kazumy.realhosting.model.panel.StageType;
import project.kazumy.realhosting.model.payment.intent.PaymentIntent;
import project.kazumy.realhosting.model.plan.impl.PlanImpl;
import project.kazumy.realhosting.model.panel.ServerType;
import project.kazumy.realhosting.model.plan.pre.PrePlan;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * @author Vinícius Albert
 * @see PlanImpl ;
 */
public interface Plan {

    /**
     * Retorna o identificador único de cada plano.
     * Geralmente criado em 15 dígitos.
     *
     * @return identificador único.
     */
    String getId();

    /**
     * Retorna a data em que o plano foi criado.
     *
     * @return data de criação do plano.
     */
    LocalDateTime getCreation();

    /**
     * Retorna a data em que o plano foi pago.
     *
     * @return data de pagamento do plano.
     */
    LocalDateTime getPayment();

    /**
     * Retorna a data em que o plano vai expirar.
     *
     * @return data de expiração do plano.
     */
    LocalDateTime getExpiration();

    /**
     * Retorna a intenção de pagamento do plano.
     * Comumente usado para diferenciar adquirimento de plano,
     * renovações ou aprimoramento de planos.
     *
     * @see PaymentIntent
     * @return intenção de pagamento do plano.
     */
    PaymentIntent getPaymentIntent();

    void setPaymentIntent(PaymentIntent paymentIntent);

    void setStageType(StageType stageType);

    /**
     * Retorna o estado atual em que o plano se encontra.
     *
     * @see StageType
     * @return estado atual do plano.
     */
    StageType getStageType();

    /**
     * Retorna o tipo do servidor que o cliente escolheu.
     *
     * @see ServerType
     * @return tipo específico de servidor.
     */
    ServerType getServerType();

    /**
     * Retorna um plano pré moldado, contendo informações básicas.
     *
     * @return um pré plano.
     */
    PrePlan getPrePlan();

    void setPrePlan(PrePlan prePlan);

    /**
     *
     * Obtém todos os dados necessários novo plano
     * para salvar no arquivo de dados do cliente
     * localizado em 'services/payment/client/(client.id).yml'.
     *
     * @param client instância do cliente que está efetuando o pagamento.
     */
    void savePlan(Client client);

    /**
     *
     * Obtém todos os dados necessários novo plano
     * para salvar no arquivo de dados do cliente
     * localizado em 'services/payment/client/(client.id).yml'.
     *
     * @param client instância do cliente que está efetuando o pagamento.
     * @param success callback para quando os dados terminarem de ser salvos.
     */
    void savePlan(Client client, Consumer<Plan> success);
}

package project.kazumy.realhosting.model.entity.client;

import project.kazumy.realhosting.model.panel.Panel;
import project.kazumy.realhosting.model.plan.Plan;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Vinícius Albert
 */
public interface Client {

    /**
     * Retorna o identificador único de cada cliente.
     *
     * @return identificador do cliente.
     */
    String getId();

    String getName();

    String getLastname();

    String getUsername();

    String getEmail();

    void setName(String name);

    void setLastname(String lastname);

    void setUsername(String username);

    void setEmail(String email);

    void setQrData(String qrData);

    Panel getPanel();

    /**
     * Retorna uma lista contendo todos os planos do cliente.
     * Pode ser nulo, caso o cliente não tenha nenhum plano.
     *
     * @return lista de planos.
     */
    Set<Plan> getPlans();

    /**
     * Espera a efetivação do pagamento por parte do cliente
     * sobre o plano que está sendo adquirido.
     *
     * @param plan plano especificado.
     * @param success executado quando o pagamento é efetivado e encontrado.
     */
    /*void purchase(Plan plan, Consumer<Client> success);*/
}

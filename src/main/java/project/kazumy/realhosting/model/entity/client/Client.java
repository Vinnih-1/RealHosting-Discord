package project.kazumy.realhosting.model.entity.client;

import org.simpleyaml.configuration.file.YamlFile;
import project.kazumy.realhosting.model.plan.Plan;

import java.util.List;
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

    /**
     * Retorna uma lista contendo todos os planos do cliente.
     * Pode ser nulo, caso o cliente não tenha nenhum plano.
     *
     * @return lista de planos.
     */
    List<Plan> getPlan();

    /**
     * Retorna o arquivo de configuração do cliente
     * localizado na pasta 'services/payment/cliente/(client.id).yml'
     *
     * @return arquivo de configuração do cliente.
     */
    YamlFile getConfig();

    /**
     * Espera a efetivação do pagamento por parte do cliente
     * sobre o plano que está sendo adquirido.
     *
     * @param plan plano especificado.
     * @param success executado quando o pagamento é efetivado e encontrado.
     */
    void purchase(Plan plan, Consumer<Client> success);
}

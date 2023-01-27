package project.kazumy.realhosting.model.entity.client.manager;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;
import project.kazumy.realhosting.model.entity.client.Client;
import project.kazumy.realhosting.model.entity.client.impl.ClientImpl;
import project.kazumy.realhosting.model.entity.client.repository.ClientRepository;
import project.kazumy.realhosting.model.plan.manager.PlanManager;

/**
 * @author Vinícius Albert
 */
@Data(staticConstructor = "of")
public class ClientManager {

    private final ClientRepository repository;
    private final PlanManager planManager;

    /**
     * Cria um arquivo de um novo cliente na pasta especificada
     * em 'services/payment/client' ditado com a extensão .yml
     * para guardar todos os dados e planos do cliente especificado.
     *
     * @param id identificador do novo cliente que está a ser criado.
     * @return uma instância do novo cliente.
     */
    @SneakyThrows
    public Client createNewClient(String id) {
        val client = new ClientImpl(id, null);
        repository.save(client);
        return client;
    }

    /**
     * Consulta na lista e busca pelo cliente que contenha o
     * identificador especificado. Caso o cliente não exista,
     * ele cria um e retorna-o mesmo assim.
     *
     * @param id identificador do cliente.
     * @return uma instância do cliente.
     */
    public Client getClientById(String id) {
        val client = repository.findClientById(id);
        return client == null ? createNewClient(id) : client;
    }

    public void saveClient(Client client) {
        repository.save(client);
    }
}

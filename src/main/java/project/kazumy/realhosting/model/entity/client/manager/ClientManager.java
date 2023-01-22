package project.kazumy.realhosting.model.entity.client.manager;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import project.kazumy.realhosting.model.entity.client.Client;
import project.kazumy.realhosting.model.entity.client.impl.ClientImpl;
import project.kazumy.realhosting.model.plan.PlanService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * @author Vinícius Albert
 */
@Data(staticConstructor = "of")
public class ClientManager {

    private static ClientManager instance;

    private final PlanService planService;
    private final Set<Client> clientList = new HashSet<>();

    /**
     * Quando instanciado, carrega todos os arquivos yaml que estejam
     * na pasta localizada em 'services/payment/client', e coloca-os
     * para a lista clientList.
     *
     */
    public ClientManager load() {
        val folder = new File("services/payment/client");
        loadClient(folder);

        return this;
    }

    /**
     * Carrega todos os clientes para uma lista.
     *
     * @param folder pasta em que os arquivos estão localizados.
     */
    private void loadClient(File folder) {
        val client = new AtomicInteger();
        val initialTime = System.currentTimeMillis();

        Arrays.stream(folder.listFiles())
                .filter(file -> file.getName().endsWith(".yml"))
                .map(file -> new YamlFile(new File(folder, file.getName())))
                .map(config -> {
                    try {
                        config.load();
                        return config.getConfigurationSection("client");
                    } catch (InvalidConfigurationException | IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(config -> config.getString("id") != null)
                .forEach(config -> {
                    clientList.add(new ClientImpl(
                       config.getString("id"),
                       planService.getPlanByClientId(config.getString("id")),
                       new YamlFile(new File("services/payment/client", config.getString("id") + ".yml"))
                    ));
                    client.incrementAndGet();
                });
        Logger.getGlobal().info(String.format("Um total de %s clientes carregados em %sms", client.get(), System.currentTimeMillis() - initialTime));
    }

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
        val clientConfig = new YamlFile(new File("services/payment/client", id + ".yml"));
        clientConfig.createOrLoad();
        clientConfig.addDefault("client.id", id);
        clientConfig.save();
        val client = new ClientImpl(id, null, clientConfig);
        clientList.add(client);

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
        return clientList.stream()
                .filter(client -> client.getId().equals(id))
                .findFirst().orElse(createNewClient(id));
    }

    public static ClientManager getInstance() {
        if (instance == null) {
            instance = new ClientManager(null);
        }
        return instance;
    }
}

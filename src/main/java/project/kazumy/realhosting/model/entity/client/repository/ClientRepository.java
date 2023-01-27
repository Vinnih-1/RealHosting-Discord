package project.kazumy.realhosting.model.entity.client.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.Data;
import project.kazumy.realhosting.model.entity.client.Client;
import project.kazumy.realhosting.model.entity.client.adapter.ClientAdapter;

@Data(staticConstructor = "of")
public class ClientRepository {

    private final SQLExecutor executor;

    public void save(Client client) {
        executor.updateQuery("INSERT INTO clients (id, firstname, lastname, username, email) " +
                "VALUES(?, ?, ?, ?, ?)", statement -> {
            statement.set(1, client.getId());
            statement.set(2, client.getName());
            statement.set(3, client.getLastname());
            statement.set(4, client.getUsername());
            statement.set(5, client.getEmail());
        });
    }

    public Client findClientById(String id) {
        return executor.resultOneQuery("SELECT * FROM clients WHERE id = ?", statement -> {
            statement.set(1, id);
        }, ClientAdapter.class);
    }
}

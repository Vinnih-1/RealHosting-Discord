package project.kazumy.realhosting.model.entity.client.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import project.kazumy.realhosting.model.entity.client.Client;
import project.kazumy.realhosting.model.entity.client.impl.ClientImpl;

public class ClientAdapter implements SQLResultAdapter<Client> {

    @Override
    public Client adaptResult(SimpleResultSet result) {
        return new ClientImpl(result.get("id"), null);
    }
}

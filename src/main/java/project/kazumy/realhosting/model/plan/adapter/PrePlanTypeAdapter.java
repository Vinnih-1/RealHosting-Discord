package project.kazumy.realhosting.model.plan.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;

public class PrePlanTypeAdapter implements SQLResultAdapter<String> {

    @Override
    public String adaptResult(SimpleResultSet result) {
        return result.get("type");
    }
}

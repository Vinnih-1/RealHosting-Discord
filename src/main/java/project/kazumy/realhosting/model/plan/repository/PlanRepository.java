package project.kazumy.realhosting.model.plan.repository;

import com.henryfabio.sqlprovider.executor.SQLExecutor;
import lombok.Data;
import project.kazumy.realhosting.model.plan.Plan;
import project.kazumy.realhosting.model.plan.adapter.PlanAdapter;
import project.kazumy.realhosting.model.plan.adapter.PrePlanTypeAdapter;

import java.util.Set;

@Data(staticConstructor = "of")
public class PlanRepository {

    private final SQLExecutor executor;

    public void save(Plan plan) {
        executor.updateQuery("INSERT INTO plans (id, owner, intent, type, stage, server, creation, payment, expiration, external_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", statement -> {
            statement.set(1, plan.getId());
            statement.set(2, plan.getOwner());
            statement.set(3, plan.getPaymentIntent().toString());
            statement.set(4, plan.getPrePlan().getType());
            statement.set(5, plan.getStageType().toString());
            statement.set(6, plan.getServerType().toString());
            statement.set(7, plan.getCreation().toString());
            statement.set(8, plan.getPayment().toString());
            statement.set(9, plan.getExpiration().toString());
            statement.set(10, plan.getExternalId());
        });
    }

    public Plan findPlanById(String id) {
        return executor.resultOneQuery("SELECT * FROM plans WHERE id = ?", statement -> {
            statement.set(1, id);
        }, PlanAdapter.class);
    }

    public Plan findPlanByExternalId(String externalId) {
        return executor.resultOneQuery("SELECT * FROM plans WHERE external_id = ?", statement -> {
            statement.set(1, externalId);
        }, PlanAdapter.class);
    }

    public Set<Plan> findPlansByClientId(String id) {
        return executor.resultManyQuery("SELECT * FROM plans WHERE owner = ?", statement -> {
            statement.set(1, id);
        }, PlanAdapter.class);
    }

    public String findPrePlanTypeById(String id) {
        return executor.resultOneQuery("SELECT type FROM plans WHERE id = ?", statement -> {
            statement.set(1, id);
        }, PrePlanTypeAdapter.class);
    }
}

package project.kazumy.realhosting.model.plan.adapter;

import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;
import project.kazumy.realhosting.model.panel.ServerType;
import project.kazumy.realhosting.model.panel.StageType;
import project.kazumy.realhosting.model.payment.intent.PaymentIntent;
import project.kazumy.realhosting.model.plan.Plan;
import project.kazumy.realhosting.model.plan.impl.PlanImpl;

import java.time.LocalDateTime;

public class PlanAdapter implements SQLResultAdapter<Plan> {

    @Override
    public Plan adaptResult(SimpleResultSet result) {
        return PlanImpl.builder()
                .id(result.get("id"))
                .owner(result.get("owner"))
                .externalId(result.get("external_id") != null ? result.get("external_id") : "")
                .paymentIntent(PaymentIntent.valueOf(result.get("intent")))
                .prePlan(null)
                .stageType(StageType.valueOf(result.get("stage")))
                .serverType(ServerType.valueOf(result.get("server")))
                .creation(LocalDateTime.parse(result.get("creation")))
                .payment(LocalDateTime.parse(result.get("payment")))
                .expiration(LocalDateTime.parse(result.get("expiration")))
                .build();
    }
}

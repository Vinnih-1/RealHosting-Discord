import lombok.SneakyThrows;
import lombok.val;
import org.simpleyaml.configuration.file.YamlFile;
import project.kazumy.realhosting.discord.services.panel.ServerType;
import project.kazumy.realhosting.discord.services.plan.PaymentIntent;
import project.kazumy.realhosting.discord.services.plan.StageType;
import project.kazumy.realhosting.discord.services.plan.impl.PlanImpl;
import project.kazumy.realhosting.discord.services.plan.manager.PlanManager;
import project.kazumy.realhosting.discord.services.plan.pre.PrePlan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainRealTest {

    private static List<PrePlan> prePlanList = new ArrayList<>();

    @SneakyThrows
    public static void main(String[] args) {
        val c = new YamlFile("services/payment/client/519070128341.yml");
        c.load();

        val planManager = new PlanManager();
        planManager.service(null);

        c.getConfigurationSection("client.plans")
                .getKeys(false).stream()
                .map(key -> c.getConfigurationSection("client.plans." + key))
                .map(config -> PlanImpl.builder()
                        .create(null)
                        .payment(null)
                        .expiration(null)
                        .paymentIntent(PaymentIntent.valueOf(config.getString("intent")))
                        .serverType(ServerType.valueOf(config.getString("server")))
                        .stageType(StageType.valueOf(config.getString("stage")))
                        .prePlan(planManager.getPrePlanByType(config.getString("type")))
                        .build())
                .collect(Collectors.toList());
    }
}

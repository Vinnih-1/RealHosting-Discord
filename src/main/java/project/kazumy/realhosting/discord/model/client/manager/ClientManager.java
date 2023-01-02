package project.kazumy.realhosting.discord.model.client.manager;

import lombok.SneakyThrows;
import lombok.val;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import project.kazumy.realhosting.discord.model.client.Client;
import project.kazumy.realhosting.discord.model.client.impl.ClientImpl;
import project.kazumy.realhosting.discord.services.panel.ServerType;
import project.kazumy.realhosting.discord.services.plan.PaymentIntent;
import project.kazumy.realhosting.discord.services.plan.Plan;
import project.kazumy.realhosting.discord.services.plan.StageType;
import project.kazumy.realhosting.discord.services.plan.impl.PlanImpl;
import project.kazumy.realhosting.discord.services.plan.manager.PlanManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClientManager {

    private PlanManager planManager;

    private final List<Client> clientList = new ArrayList<>();

    public ClientManager(PlanManager planManager) {
        val folder = new File("services/payment/client");
        loadClient(folder);

        this.planManager = planManager;
    }

    private void loadClient(File folder) {
        Arrays.stream(folder.listFiles())
                .filter(file -> file.getName().endsWith(".yml"))
                .map(file -> new YamlFile(file.getName()))
                .map(config -> {
                    try {
                        config.load();
                        return config.getConfigurationSection("client");
                    } catch (InvalidConfigurationException | IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(config -> {
                    clientList.add(new ClientImpl(
                       config.getString("id"),
                       getPlanByClientId(config.getString("id")),
                       new YamlFile(config.getString("id"))
                    ));
                });
    }

    @SneakyThrows
    public List<Plan> getPlanByClientId(String id) {
        val configPlan = new YamlFile("services/payment/client/519070128341.yml");
        configPlan.load();

        return configPlan.getConfigurationSection("client.plans")
                .getKeys(false).stream()
                .map(key -> configPlan.getConfigurationSection("client.plans." + key))
                .map(config -> PlanImpl.builder()
                        .creation(LocalDateTime.parse(config.getString("creation")))
                        .payment(LocalDateTime.parse(config.getString("payment")))
                        .expiration(LocalDateTime.parse(config.getString("expiration")))
                        .paymentIntent(PaymentIntent.valueOf(config.getString("intent")))
                        .serverType(ServerType.valueOf(config.getString("server")))
                        .stageType(StageType.valueOf(config.getString("stage")))
                        .prePlan(planManager.getPrePlanByType(config.getString("type")))
                        .build())
                .collect(Collectors.toList());
    }
}

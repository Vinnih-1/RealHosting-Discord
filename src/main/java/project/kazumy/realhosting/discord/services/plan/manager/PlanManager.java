package project.kazumy.realhosting.discord.services.plan.manager;

import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import project.kazumy.realhosting.discord.services.BaseService;
import project.kazumy.realhosting.discord.services.plan.pre.PrePlan;
import project.kazumy.realhosting.discord.services.plan.pre.PlanHardware;
import project.kazumy.realhosting.discord.services.plan.impl.PrePlanImpl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class PlanManager extends BaseService {

    private final List<PrePlan> prePlanList = new ArrayList<>();

    public PlanManager() {
        super(2L);
    }

    private void loadPrePlan(File folder, File resources) {
        val plan = new AtomicInteger();
        val initialTime = System.currentTimeMillis();

        Arrays.stream(folder.listFiles())
                .filter(file -> file.getName().endsWith(".yml"))
                .map(file -> new YamlFile(new File(folder, file.getName())))
                .map(config -> {
                    try {
                        config.load();
                        return config.getConfigurationSection("plan");
                    } catch (InvalidConfigurationException | IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(config -> {
                    prePlanList.add(new PrePlanImpl(
                            config.getString("id"),
                            config.getString("title"),
                            config.getString("type"),
                            config.getString("description"),
                            new BigDecimal(config.getString("price")),
                            Emoji.fromUnicode(config.getString("emoji")),
                            new File(resources, config.getString("logo")),
                            PlanHardware.builder()
                                    .cpu(config.getLong("hardware.cpu"))
                                    .ram(config.getLong("hardware.ram"))
                                    .disk(config.getLong("hardware.disk"))
                                    .backup(config.getLong("hardware.backup"))
                                    .database(config.getLong("hardware.database"))
                                    .build())
                    );
                    plan.incrementAndGet();
                });
        Logger.getGlobal().info(String.format("Um total de %s planos carregados em %sms", plan.get(), System.currentTimeMillis() - initialTime));
    }

    public PrePlan getPrePlanByType(String type) {
        return prePlanList.stream()
                .filter(prePlan -> prePlan.getType().equals(type))
                .findAny().orElse(null);
    }

    @Override
    public BaseService service(JDA jda) {
        val folder = new File("services/payment/plan");
        val resources = new File("resources");
        if (!folder.exists()) System.out.println(folder.mkdirs());
        if (!resources.exists()) System.out.println(resources.mkdirs());
        loadPrePlan(folder, resources);

        return this;
    }
}
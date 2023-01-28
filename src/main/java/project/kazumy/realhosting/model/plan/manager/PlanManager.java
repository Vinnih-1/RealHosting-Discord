package project.kazumy.realhosting.model.plan.manager;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import project.kazumy.realhosting.model.payment.Payment;
import project.kazumy.realhosting.model.plan.Plan;
import project.kazumy.realhosting.model.plan.impl.PrePlanImpl;
import project.kazumy.realhosting.model.plan.pre.PlanHardware;
import project.kazumy.realhosting.model.plan.pre.PrePlan;
import project.kazumy.realhosting.model.plan.repository.PlanRepository;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * @author Vinícius Albert
 */
@RequiredArgsConstructor
public class PlanManager {

    private final PlanRepository repository;
    @Setter private Payment payment;

    private final List<PrePlan> prePlanList = new ArrayList<>();

    /**
     * Carrega todos os planos pré-moldados da pasta
     * localizada em 'services/payment/plan' com a extensão .yml
     *
     */
    public PlanManager loadPrePlan() {
        val folder = new File("services/payment/plan");
        val resources = new File("resources");
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
        return this;
    }

    /**
     * Retorna um plano pré-moldado pelo tipo do plano.
     *
     * @param type tipo do plano.
     * @return uma instância do plano.
     */
    public PrePlan getPrePlanByType(String type) {
        return prePlanList.stream()
                .filter(prePlan -> prePlan.getType().equals(type))
                .findAny().orElse(null);
    }

    public PrePlan getPrePlanById(String id) {
        return prePlanList.stream()
                .filter(prePlan -> prePlan.getId().equals(id))
                .findAny().orElse(null);
    }

    /**
     * Retorna todos os planos que um cliente tem
     * utilizando o arquivo de configuração do cliente
     * localizado na pasta 'services/payment/client/(client.id).yml'
     *
     * @param id identificador único do cliente.
     * @return uma lista contendo todos os planos encontrados.
     *
     * Pode returnar nulo, caso o cliente não detenha nenhum plano.
     */
    @SneakyThrows
    public Set<Plan> getPlanByClientId(String id) {
        return repository.findPlansByClientId(id);
    }

    public Plan getPlanById(String id) {
        return repository.findPlanById(id);
    }

    public String getPrePlanTypeFromPlanById(String planId) {
        return repository.findPrePlanTypeById(planId);
    }

    public void savePlan(Plan plan) {
        repository.save(plan);
    }

    public String purchase(Plan plan, Consumer<Void> success) {
        val request = payment.request(plan);
        payment.wait(plan.getId(), success);
        return request;
    }
}
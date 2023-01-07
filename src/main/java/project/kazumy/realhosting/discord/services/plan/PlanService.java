package project.kazumy.realhosting.discord.services.plan;

import lombok.SneakyThrows;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import project.kazumy.realhosting.discord.services.BaseService;
import project.kazumy.realhosting.model.panel.ServerType;
import project.kazumy.realhosting.model.payment.PaymentIntent;
import project.kazumy.realhosting.model.plan.Plan;
import project.kazumy.realhosting.model.panel.StageType;
import project.kazumy.realhosting.model.plan.impl.PlanImpl;
import project.kazumy.realhosting.model.plan.pre.PrePlan;
import project.kazumy.realhosting.model.plan.pre.PlanHardware;
import project.kazumy.realhosting.model.plan.impl.PrePlanImpl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Vinícius Albert
 */
public class PlanService extends BaseService {

    private final List<PrePlan> prePlanList = new ArrayList<>();

    /**
     * Carrega esta classe de serviço e diz qual
     * a prioridade na fila de carregamento.
     */
    public PlanService() {
        super(2L);
    }

    /**
     * Carrega todos os planos pré-moldados da pasta
     * localizada em 'services/payment/plan' com a extensão .yml
     *
     * @param folder pasta em que se encontra os arquivos.
     * @param resources arquivo de imagem de cada plano.
     */
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
    public List<Plan> getPlanByClientId(String id) {
        if (!hasPlanByClientId(id)) return null;
        val clientConfig = new YamlFile(String.format("services/payment/client/%s.yml", id));
        clientConfig.load();
        return clientConfig.getConfigurationSection("client.plans")
                .getKeys(false).stream()
                .map(key -> clientConfig.getConfigurationSection("client.plans." + key))
                .map(config -> PlanImpl.builder()
                        .creation(LocalDateTime.parse(config.getString("creation")))
                        .payment(LocalDateTime.parse(config.getString("payment")))
                        .expiration(LocalDateTime.parse(config.getString("expiration")))
                        .paymentIntent(PaymentIntent.valueOf(config.getString("intent")))
                        .serverType(ServerType.valueOf(config.getString("server")))
                        .stageType(StageType.valueOf(config.getString("stage")))
                        .prePlan(getPrePlanByType(config.getString("type")))
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Verifica se o identificador do cliente
     * especificado contém algum plano.
     *
     * @param id identificador único do cliente.
     * @return verdadeiro ou falso, dependendo se o cliente tem ou não algum plano.
     */
    @SneakyThrows
    public boolean hasPlanByClientId(String id) {
        val folder = new File("services/payment/client");
        if (!folder.exists()) return false;
        val yamlFile = new YamlFile(new File(folder, id + ".yml"));
        yamlFile.load();
        return yamlFile.getConfigurationSection("client.plans") != null;
    }

    /**
     * Executado na inicialização da classe de serviços
     *
     * @param jda uma instância do JDA.
     * @return uma instância da classe BaseService.
     */
    @Override
    public BaseService service(JDA jda) {
        val folder = new File("services/payment/plan");
        val resources = new File("resources");
        if (!folder.exists()) folder.mkdirs();
        if (!resources.exists()) resources.mkdirs();
        loadPrePlan(folder, resources);

        return this;
    }
}
package project.kazumy.realhosting.model.entity.client.impl;

import lombok.*;
import org.simpleyaml.configuration.file.YamlFile;
import project.kazumy.realhosting.model.entity.client.Client;
import project.kazumy.realhosting.model.panel.Panel;
import project.kazumy.realhosting.model.payment.Payment;
import project.kazumy.realhosting.model.plan.Plan;
import project.kazumy.realhosting.model.plan.PlanService;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Vinícius Albert
 */
@Getter
@ToString
@RequiredArgsConstructor
public class ClientImpl extends Payment implements Client {

    private final String id;
    private final List<Plan> plans;
    private final YamlFile config;
    private final PlanService planService;
    private final Panel panel = new Panel().authenticate();
    @Setter private String name, lastname, username, email, qrData;

    /**
     * Espera a efetivação do pagamento por parte do cliente
     * sobre o plano adquirido.
     *
     * @param plan plano especificado.
     * @param success executado quando o pagamento é efetivado e encontrado.
     */
    @Override
    @SneakyThrows
    public void purchase(Plan plan, Consumer<Client> success) {
        val request = this.request(plan, this);

        System.out.println(this.getQrData());
        request.wait(payment -> {
            plan.savePlan(this);
            success.accept(this);
        }, planService, this);
    }

    @Override
    @SneakyThrows
    public void saveData() {
        config.createOrLoad();
        config.set("client.name", name);
        config.set("client.lastname", lastname);
        config.set("client.username", username);
        config.set("client.email", email);
        config.save();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientImpl client = (ClientImpl) o;
        return id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

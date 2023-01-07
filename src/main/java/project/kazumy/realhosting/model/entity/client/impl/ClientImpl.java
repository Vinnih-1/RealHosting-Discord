package project.kazumy.realhosting.discord.model.entity.client.impl;

import lombok.*;
import org.simpleyaml.configuration.file.YamlFile;
import project.kazumy.realhosting.discord.model.entity.client.Client;
import project.kazumy.realhosting.discord.model.panel.Panel;
import project.kazumy.realhosting.discord.model.payment.Payment;
import project.kazumy.realhosting.discord.model.plan.Plan;

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
    private final List<Plan> plan;
    private final YamlFile config;
    private final Panel panel = new Panel().authenticate();

    /**
     * Espera a efetivação do pagamento por parte do cliente
     * sobre o plano que está sendo adquirido.
     *
     * @param plan plano especificado.
     * @param success executado quando o pagamento é efetivado e encontrado.
     */
    @Override
    @SneakyThrows
    public void purchase(Plan plan, Consumer<Client> success) {
        val request = this.request(plan);
        System.out.println(request.getQrData());
        request.wait(payment -> {
            plan.savePlan(this);
            success.accept(this);
        });
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

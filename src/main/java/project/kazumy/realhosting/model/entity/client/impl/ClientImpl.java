package project.kazumy.realhosting.model.entity.client.impl;

import lombok.*;
import project.kazumy.realhosting.model.entity.client.Client;
import project.kazumy.realhosting.model.panel.Panel;
import project.kazumy.realhosting.model.plan.Plan;

import java.util.Set;

/**
 * @author Vinícius Albert
 */
@Getter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ClientImpl implements Client {

    @EqualsAndHashCode.Include private final String id;

    private final Set<Plan> plans;
    private final Panel panel = new Panel().authenticate();
    @Setter private String name, lastname, username, email, qrData;
}

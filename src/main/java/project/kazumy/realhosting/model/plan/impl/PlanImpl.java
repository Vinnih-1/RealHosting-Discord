package project.kazumy.realhosting.model.plan.impl;

import lombok.*;
import project.kazumy.realhosting.model.panel.Panel;
import project.kazumy.realhosting.model.panel.ServerType;
import project.kazumy.realhosting.model.panel.StageType;
import project.kazumy.realhosting.model.payment.intent.PaymentIntent;
import project.kazumy.realhosting.model.plan.Plan;
import project.kazumy.realhosting.model.plan.pre.PrePlan;

import java.time.LocalDateTime;

/**
 * @author Vinícius Albert
 */
@Getter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class PlanImpl extends Panel implements Plan {

    @EqualsAndHashCode.Include private final String id;

    private final LocalDateTime creation, payment, expiration;
    @Setter private PaymentIntent paymentIntent;
    @Setter private StageType stageType;
    private final ServerType serverType;
    @Setter private PrePlan prePlan;
    private final String owner;
}

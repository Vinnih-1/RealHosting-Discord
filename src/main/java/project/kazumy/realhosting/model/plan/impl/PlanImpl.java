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
@Setter
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class PlanImpl extends Panel implements Plan {

    @EqualsAndHashCode.Include private final String id;

    private final LocalDateTime creation, payment;

    private LocalDateTime expiration;

    private final ServerType serverType;

    private final String owner;

    private PaymentIntent paymentIntent;

    private StageType stageType;

    private PrePlan prePlan;

    private String coupon, externalId;
}

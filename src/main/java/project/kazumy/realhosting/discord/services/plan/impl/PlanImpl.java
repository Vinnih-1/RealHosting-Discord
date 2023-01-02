package project.kazumy.realhosting.discord.services.plan.impl;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import project.kazumy.realhosting.discord.services.panel.ServerType;
import project.kazumy.realhosting.discord.services.plan.PaymentIntent;
import project.kazumy.realhosting.discord.services.plan.Plan;
import project.kazumy.realhosting.discord.services.plan.StageType;
import project.kazumy.realhosting.discord.services.plan.pre.PrePlan;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class PlanImpl implements Plan {

    private final LocalDateTime create, payment, expiration;
    private final PaymentIntent paymentIntent;
    private final StageType stageType;
    private final ServerType serverType;
    private final PrePlan prePlan;
}

package project.kazumy.realhosting.discord.services.plan;

import project.kazumy.realhosting.discord.services.panel.ServerType;
import project.kazumy.realhosting.discord.services.plan.pre.PrePlan;

import java.time.LocalDateTime;

public interface Plan {

    LocalDateTime getCreation();

    LocalDateTime getPayment();

    LocalDateTime getExpiration();

    PaymentIntent getPaymentIntent();

    StageType getStageType();

    ServerType getServerType();

    PrePlan getPrePlan();
}

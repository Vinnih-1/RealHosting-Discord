package project.kazumy.realhosting.discord.services.payment.plan;

import lombok.*;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.panel.ServerType;
import project.kazumy.realhosting.discord.services.panel.UserPanel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
@Builder
@Getter
public class PlanBuilder {

    private String planId, skuId, userId, userAsTag, description, logo, title;

    private UserPanel userPanel;

    @Setter private PlanType planType;

    @Setter private ServerType serverType;

    @Setter private StageType stageType;

    private BigDecimal price;

    private Configuration config;

    private boolean enabled;

    private UnicodeEmoji emojiUnicode;

    @Setter private LocalDateTime createDate, paymentDate, expirationDate;

    @SneakyThrows
    public void registerPlan() {
        config.addDefaults(consumer -> {
            consumer.addDefault("plan.author.authorId", userId);
            consumer.addDefault("plan.author.authorAsTag", userAsTag);
            consumer.addDefault("plan.planId", planId);
            consumer.addDefault("plan.skuId", skuId);
            consumer.addDefault("plan.title", title);
            consumer.addDefault("plan.price", price.toString());
            consumer.addDefault("plan.description", description);
            consumer.addDefault("plan.logo", logo);
            consumer.addDefault("plan.emojiUnicode", emojiUnicode.getAsCodepoints());
            consumer.addDefault("plan.createDate", createDate.toString());
            consumer.addDefault("plan.planType", planType.toString());
            consumer.addDefault("plan.serverType", serverType.toString());
            consumer.addDefault("plan.stageType", stageType.toString());
            consumer.addDefault("plan.enabled", enabled);
        });
        config.save();
    }

    @SneakyThrows
    public void validatePlan() {
        enabled = true;
        config.set("plan.paymentDate", paymentDate.toString());
        config.set("plan.expirationDate", expirationDate.toString());
        config.set("plan.enabled", true);
        config.set("plan.stageType", stageType.toString());
        config.save();
    }

    @SneakyThrows
    public void updateStageType() {
        config.set("plan.stageType", stageType.toString());
        config.save();
    }

    @SneakyThrows
    public void expirePlan() {
        config.set("plan.enabled", false);
        config.set("plan.stageType", stageType.toString());
        config.save();
    }

    @SneakyThrows
    public void deletePlan() {
        config.deleteFile();
    }

    public PlanBuilder instanceConfig(Configuration config) {
        this.config = config;
        return this;
    }

    public PlanBuilder instanceConfig() {
        config = new Configuration("services/payment/plan/" + planId + ".yml");
        return this;
    }
}

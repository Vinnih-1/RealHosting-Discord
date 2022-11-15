package project.kazumy.realhosting.discord.services.payment.plan;

import lombok.*;
import net.dv8tion.jda.api.JDA;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.panel.ServerType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Logger;

@ToString
@Builder
@Data
public class PlanBuilder {

    private PlanData planData;

    @Getter private boolean notified;

    private PlanType planType;
    private ServerType serverType;
    private StageType stageType;
    PaymentIntent paymentIntent;

    private BigDecimal price;

    private Configuration config;

    private LocalDateTime createDate, paymentDate, expirationDate;

    @SneakyThrows
    public void registerPlan() {
        config.addDefaults(consumer -> {
            if (stageType == StageType.CHOOSING_SERVER) {
                consumer.set("plan.author.authorId", planData.getUserId());
                consumer.set("plan.author.authorAsTag", planData.getUserAsTag());
                consumer.set("plan.planId", planData.getPlanId());
                consumer.set("plan.externalReference", planData.getExternalReference());
                consumer.set("plan.createDate", createDate.toString());
                consumer.set("plan.serverType", serverType.toString());
                consumer.set("plan.stageType", stageType.toString());
                consumer.set("plan.paymentIntent", paymentIntent.toString());
                return;
            }
            consumer.addDefault("plan.skuId", planData.getSkuId());
            consumer.addDefault("plan.title", planData.getTitle());
            consumer.addDefault("plan.price", price.toString());
            consumer.addDefault("plan.description", planData.getDescription());
            consumer.addDefault("plan.logo", planData.getLogo());
            consumer.addDefault("plan.emojiUnicode", planData.getEmojiUnicode().getAsCodepoints());
            consumer.addDefault("plan.planType", planType.toString());
        });
        config.save();
    }

    public void enablePlan() {
        if (paymentIntent == PaymentIntent.CREATE_PLAN) {
            this.setPaymentDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
            this.setExpirationDate(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
        }
        this.setNotified(true);
        this.setStageType(StageType.ACTIVED);
        this.setPaymentIntent(PaymentIntent.CREATE_USER);
        this.setExpirationDate(this.getExpirationDate().plusDays(30L));
        this.getPlanData().setExternalReference("");

        saveConfig();
    }

    public void giveBuyerTag(JDA jda, Configuration config) {
        val guild = jda.getGuildById(config.getString("bot.guild.id"));
        val role = guild.getRoles().stream()
                .filter(roles -> roles.getId().equals("896873613200867338")).findFirst().get();

        guild.addRoleToMember(jda.getUserById(planData.getUserId()), role).queue(success -> {
            Logger.getGlobal().info("A tag Cliente foi cedida ao usuário " + planData.getUserAsTag());
        });
    }

    public void disablePlan() {
        this.setStageType(StageType.SUSPENDED);
        this.setPaymentIntent(PaymentIntent.NONE);

        saveConfig();
    }

    @SneakyThrows
    public void saveConfig() {
        if (paymentDate != null)
            config.set("plan.paymentDate", paymentDate.toString());
        if (expirationDate != null)
            config.set("plan.expirationDate", expirationDate.toString());
        config.set("plan.stageType", stageType.toString());
        config.set("plan.paymentIntent", paymentIntent.toString());
        config.set("plan.description", planData.getDescription());
        config.set("plan.externalReference", planData.getExternalReference());
        config.set("plan.notified", this.isNotified());
        config.save();
    }

    @SneakyThrows
    public void updatePaymentIntent(PaymentIntent paymentIntent) {
        this.setPaymentIntent(paymentIntent);
        config.set("plan.paymentIntent", paymentIntent.toString());
        config.save();
    }

    @SneakyThrows
    public void updateStageType(StageType stageType) {
        this.setStageType(stageType);
        config.set("plan.stageType", stageType.toString());
        config.save();
    }

    @SneakyThrows
    public void deletePlan() {
        config.deleteFile();
        System.out.println(InitBot.paymentManager.getPlans().remove(this));
    }

    public PlanBuilder instanceConfig(Configuration config) {
        this.config = config;
        return this;
    }

    public PlanBuilder instanceConfig() {
        config = new Configuration("services/payment/plan/" + planData.getPlanId() + ".yml");
        return this;
    }
}

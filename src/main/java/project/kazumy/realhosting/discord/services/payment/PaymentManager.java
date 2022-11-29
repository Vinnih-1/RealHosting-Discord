package project.kazumy.realhosting.discord.services.payment;

import io.nayuki.qrcodegen.QrCode;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.apache.commons.lang.RandomStringUtils;
import project.kazumy.realhosting.discord.InitBot;
import project.kazumy.realhosting.discord.configuration.Configuration;
import project.kazumy.realhosting.discord.services.BaseService;
import project.kazumy.realhosting.discord.services.panel.ServerType;
import project.kazumy.realhosting.discord.services.payment.plan.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PaymentManager extends BaseService {

    @Getter @Setter
    private Set<PlanBuilder> plans;

    @Getter
    private PaymentMP paymentMP;

    public PaymentManager() {
        super(2L);
    }
    public void loadPlans() {
        val planFolder = new File("services/payment/plan/");

        if (!planFolder.exists()) return;
        Arrays.stream(planFolder.listFiles())
                .filter(file -> file.getName().endsWith(".yml"))
                .map(file -> new Configuration("services/payment/plan/" + file.getName()).buildIfNotExists())
                .forEach(config -> {
                    try {
                        if (config.getString("plan.paymentIntent") == null) {
                            config.set("plan.paymentIntent", "NONE");
                            config.set("plan.externalReference", "");
                            config.save();
                        }

                        val createDate = config.getString("plan.createDate");
                        val paymentDate = config.getString("plan.paymentDate");
                        val expireDate = config.getString("plan.expirationDate");

                        val plan = PlanBuilder.builder()
                                .planData(PlanData.builder()
                                        .externalReference(config.getString("plan.externalReference"))
                                        .title(config.getString("plan.title"))
                                        .description(config.getString("plan.description"))
                                        .logo(config.getString("plan.logo"))
                                        .skuId(config.getString("plan.skuId"))
                                        .planId(config.getString("plan.planId"))
                                        .userId(config.getString("plan.author.authorId"))
                                        .userAsTag(config.getString("plan.author.authorAsTag"))
                                        .emojiUnicode(Emoji.fromUnicode(config.getString("plan.emojiUnicode")))
                                        .build())
                                .price(new BigDecimal(config.getString("plan.price")))
                                .planType(PlanType.valueOf(config.getString("plan.planType")))
                                .stageType(StageType.valueOf(config.getString("plan.stageType")))
                                .serverType(ServerType.valueOf(config.getString("plan.serverType")))
                                .paymentIntent(PaymentIntent.valueOf(config.getString("plan.paymentIntent")))
                                .createDate(createDate != null ? LocalDateTime.parse(createDate) : null)
                                .paymentDate(paymentDate != null ? LocalDateTime.parse(paymentDate) : null)
                                .expirationDate(expireDate != null ? LocalDateTime.parse(expireDate) : null)
                                .notified(config.getBoolean("plan.notified"))
                                .build().instanceConfig(config);
                        Logger.getGlobal().info("O plano " + config.getString("plan.planId") + " foi carregado para a memória: "
                                + plans.add(plan));
                    } catch (Exception e) {
                        Logger.getGlobal().severe(String.format("Houve uma falha ao carregar o plano %s ", config.getString("plan.planId")));
                        e.printStackTrace();
                    }
                });
    }

    public List<PlanBuilder> getExpiredPlans() {
        return getPlans().stream()
                .filter(plan -> plan.getExpirationDate() != null)
                .filter(plan -> plan.getExpirationDate().isBefore(LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))))
                .collect(Collectors.toList());
    }

    public List<PlanBuilder> getExpiredPlans(Long days) {
        return getPlans().stream()
                .filter(plan -> plan.getExpirationDate() != null)
                .filter(plan -> plan.getExpirationDate().minusDays(days).isBefore(LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))))
                .collect(Collectors.toList());
    }

    public boolean hasPlanByUserId(String userId) {
        return this.getPlans().stream()
                .anyMatch(plan -> plan.getPlanData().getUserId().equals(userId));
    }

    public boolean hasPlanPending(String userId) {
        return this.getPlans().stream()
                .filter(plan -> plan.getPlanData().getUserId().equals(userId))
                .filter(plan -> plan.getStageType() == StageType.ACTIVED)
                .anyMatch(plan -> plan.getPaymentIntent() == PaymentIntent.CREATE_USER);
    }

    public boolean hasPlanByPlanId(String planId) {
        return this.getPlans().stream()
                .anyMatch(plan -> plan.getPlanData().getPlanId().equals(planId));
    }

    public boolean hasPlanByExternalReference(String externalReference) {
        return this.getPlans().stream()
                .anyMatch(plan -> plan.getPlanData().getExternalReference().equals(externalReference));
    }

    public PlanBuilder getPendingPlan(String userId) {
        return this.getPlans().stream()
                .filter(plan -> plan.getPlanData().getUserId().equals(userId))
                .filter(plan -> plan.getStageType() == StageType.ACTIVED)
                .filter(plan -> plan.getPaymentIntent() == PaymentIntent.CREATE_USER).findFirst().get();
    }

    public PlanBuilder getPlanById(String planId) {
        return this.getPlans().stream()
                .filter(plan -> plan.getPlanData().getPlanId().equals(planId))
                .findFirst().get();
    }

    public PlanBuilder getPlanByExternalReference(String externalReference) {
        return this.getPlans().stream()
                .filter(plan -> plan.getPlanData().getExternalReference().equals(externalReference))
                .findAny().get();
    }

    public Set<PlanBuilder> getPlansByUserId(String userId) {
        return this.getPlans().stream()
                .filter(plan -> plan.getPlanData().getUserId().equals(userId))
                .collect(Collectors.toSet());
    }

    public void setDefaultBuyMessage(Configuration config) {
        config.addDefaults(consumer -> {
            consumer.addDefault("bot.guild.payment.title", ":desktop: Here you can choose your plan");
            consumer.addDefault("bot.guild.payment.footer", "Self-Service official of RealHosting");
            consumer.addDefault("bot.guild.payment.thumbnail", "thumbnail-url");
            consumer.addDefault("bot.guild.payment.description", "Click on any plan and see the magic happens");
            consumer.addDefault("bot.guild.payment.site", "site-url");
            consumer.addDefault("bot.guild.payment.fields.1.name", "My Name is");
            consumer.addDefault("bot.guild.payment.fields.1.value", "Zumo");
            consumer.addDefault("bot.guild.payment.fields.1.inline", true);
            consumer.addDefault("bot.guild.payment.fields.2.name", "My Job is");
            consumer.addDefault("bot.guild.payment.fields.2.value", "Supporter of RealHosting");
            consumer.addDefault("bot.guild.payment.fields.2.inline", true);
        });
    }

    public void sendServerMenu(TextChannel channel, Configuration config) {
        val embed = config.getEmbedMessageFromConfig(config, "server");
        embed.setColor(Color.GREEN);
        val menu = config.getMenuFromConfig(config, "bot.guild.server.type", "type-menu");
        channel.sendMessageEmbeds(embed.build()).addActionRow(menu.build()).queue();
    }

    public void sendBuyMenu(TextChannel channel, Configuration config, String id) {
        val embed = config.getEmbedMessageFromConfig(config, "payment");
        embed.setColor(Color.GREEN);
        val menu = config.getMenuFromConfig(config, "bot.guild.payment.plans", id);
        channel.sendMessageEmbeds(embed.build()).addActionRow(menu.build()).queue();
    }

    public String getPaymentQrCode(PaymentIntent paymentIntent, Configuration config, PlanBuilder plan) {
        val userId = config.getString("bot.payment.mercado-pago.user-id");
        val posId = config.getString("bot.payment.mercado-pago.external-pos-id");
        val accessToken = config.getString("bot.payment.mercado-pago.access-token");
        plan.updatePaymentIntent(paymentIntent);
        if (paymentIntent == PaymentIntent.UPGRADE_PLAN)
            plan.getPlanData().setExternalReference(RandomStringUtils.randomAlphanumeric(20));
        if (paymentIntent == PaymentIntent.RENEW_PLAN)
            plan.getPlanData().setExternalReference(RandomStringUtils.randomAlphanumeric(8));

        plan.saveConfig();
        val response = paymentMP.createRequestQrCode(plan, userId, posId, accessToken);
        return paymentMP.getAsText(response);
    }

    public File getPaymentQrCodeImage(String qrCodeText, PlanBuilder plan) {
        return paymentMP.getAsImage(qrCodeText, plan.getPlanData().getUserId());
    }

    public static BufferedImage toImage(QrCode qr, int scale, int border) {
        return toImage(qr, scale, border, 0xFFFFFF, 0x000000);
    }

    public static BufferedImage toImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {
        Objects.requireNonNull(qr);
        if (scale <= 0 || border < 0)
            throw new IllegalArgumentException("Value out of range");
        if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale)
            throw new IllegalArgumentException("Scale or border too large");

        BufferedImage result = new BufferedImage((qr.size + border * 2) * scale, (qr.size + border * 2) * scale, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                boolean color = qr.getModule(x / scale - border, y / scale - border);
                result.setRGB(x, y, color ? darkColor : lightColor);
            }
        }
        return result;
    }

    @Override
    public BaseService service(JDA jda, Configuration config) {
        this.setDefaultBuyMessage(config);
        this.setPlans(new HashSet<>());

        this.paymentMP = PaymentMP.of(this, InitBot.panelManager, config, jda);
        this.paymentMP.setAccessToken(InitBot.config.getString("bot.payment.mercado-pago.access-token"));

        return this;
    }
}
